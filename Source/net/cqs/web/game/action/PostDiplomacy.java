package net.cqs.web.game.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.cqs.config.ErrorCode;
import net.cqs.config.RightEnum;
import net.cqs.engine.Player;
import net.cqs.engine.diplomacy.Alliance;
import net.cqs.engine.diplomacy.Contract;
import net.cqs.engine.diplomacy.ContractDraft;
import net.cqs.engine.diplomacy.ContractParty;
import net.cqs.engine.diplomacy.ContractProposal;
import net.cqs.engine.diplomacy.ContractProposalList;
import net.cqs.engine.diplomacy.ContractType;
import net.cqs.engine.diplomacy.CreateWarDeclaration;
import net.cqs.engine.diplomacy.RemovePartyAction;
import net.cqs.engine.diplomacy.WarDeclaration;
import net.cqs.engine.messages.Message;
import net.cqs.engine.messages.PlayerMessageType;
import net.cqs.main.i18n.SystemMessageEnum;
import net.cqs.web.ParsedRequest;
import net.cqs.web.game.CqsSession;
import net.cqs.web.game.action.ActionHandler.PostHandler;

public class PostDiplomacy
{

static void init()
{
	ActionHandler.add("ContractDraft.setInitiator", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				String initiator = req.getParameter("initiator");
				if ("p".equalsIgnoreCase(initiator))
				{ session.getContractDraft().setInitiator(session.getPlayer()); }
				else if ("a".equalsIgnoreCase(initiator))
				{
					if (session.getAlliance() == null)
					{
						session.log(ErrorCode.CANNOT_MAKE_ALLIANCE_CONTRACT_NO_ALLIANCE);
						return;
						
					}
					else if (!session.getPlayer().getRank().hasAllianceRight(RightEnum.MAKE_CONTRACTS))
					{
						session.log(ErrorCode.CANNOT_MAKE_ALLIANCE_CONTRACT_NO_RIGHT);
						return;
					}
					session.getContractDraft().setInitiator(session.getAlliance());
				}
				else throw new IllegalArgumentException();
			}
		});

	ActionHandler.add("ContractDraft.setType", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				String type = req.getParameter("type");
				ContractType ctype = ContractType.valueOf(type);
				session.getContractDraft().setType(ctype);
			}
		});

	ActionHandler.add("ContractDraft.addAlliance", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				int index = Integer.parseInt(req.getParameter("index"));
				Alliance a = session.getGalaxy().findAllianceById(index);
				if (a != session.getContractDraft().getInitiator())
					session.getContractDraft().addParty(a);
			}
		});

	ActionHandler.add("ContractDraft.addPlayer", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				int index = Integer.parseInt(req.getParameter("index"));
				Player p = session.getGalaxy().findPlayerByPid(index);
				if (p != session.getContractDraft().getInitiator())
					session.getContractDraft().addParty(p);
			}
		});

	ActionHandler.add("ContractDraft.removeParty", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				int index = Integer.parseInt(req.getParameter("index"));
				ContractDraft design = session.getContractDraft();
				if ((index >=0) && (index<design.size()))
					design.removeParty(design.get(index));
				else throw new ArrayIndexOutOfBoundsException();
			}
		});

	ActionHandler.add("ContractDraft.proposal", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				ContractDraft design = session.getContractDraft();
				ContractParty[] parties = new ContractParty[design.getParties().length+1];
				parties[0] = design.getInitiator();
				for (int i = 0; i < design.getParties().length; i++)
					parties[i+1] = design.getParties()[i];
				ContractProposal cp = session.getGalaxy().getContractProposals().proposeContract(
						design.getType(), parties, session.getGalaxy().getWallClockTime());
				Player proposer = session.getPlayer();
				for (ContractParty p : cp)
				{
					if (p instanceof Player)
					{
						Player recipient = (Player) p;
						String subject = SystemMessageEnum.PROPOSE_CONTRACT.getName(recipient.getLocale());
						Message m = new Message(PlayerMessageType.CONTRACT_PROPOSAL, cp.getId(), proposer, recipient, subject, "");
						session.dropMail(recipient, m);
					}
					else if (p instanceof Alliance)
					{
						Alliance recipient = (Alliance) p;
						Message m = new Message(PlayerMessageType.CONTRACT_PROPOSAL, cp.getId(), proposer, (Player) null, "", "");
						session.dropMail(recipient, m);
					}
				}
			}
		});

	ActionHandler.add("ContractProposal.accept", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				int index = Integer.parseInt(req.getParameter("index"));
				ContractProposalList proposals = session.getGalaxy().getContractProposals();
				Contract c = proposals.acceptProposal(session.getGalaxy().getDiplomaticRelation(),
					index, session.getPlayer(), session.getGalaxy().getWallClockTime());
				if (c != null) // everyone accepted, contact was concluded
				{
					Player proposer = session.getPlayer();
					List<ContractParty> parties = c.getParties();
					for (int i = 0; i < parties.size(); i++)
					{
						if (parties.get(i) instanceof Player)
						{
							Player recipient = (Player) parties.get(i);
							String subject = SystemMessageEnum.CONTRACT_CREATION.getName(recipient.getLocale());
							Message m = new Message(PlayerMessageType.CONTRACT_CREATION, proposer, recipient, subject, "");
							session.dropMail(recipient, m);
						}
						else if (parties.get(i) instanceof Alliance)
						{
							Alliance recipient = (Alliance) parties.get(i);
							Message m = new Message(PlayerMessageType.CONTRACT_CREATION, proposer, (Player) null, "", "");
							session.dropMail(recipient, m);
						}
					}
	
				}
			}
		});

	ActionHandler.add("ContractProposal.decline", new PostHandler()
		{
			@Override
			public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
			{
				int index = Integer.parseInt(req.getParameter("index"));
				ContractProposalList proposals = session.getGalaxy().getContractProposals();
				ContractProposal cp = proposals.get(index);
				if (proposals.declineProposal(index, session.getPlayer(), session.getGalaxy().getWallClockTime()))
				{
					Player proposer = session.getPlayer();
					for (ContractParty p : cp)
					{
						if (p instanceof Player)
						{
							Player recipient = (Player) p;
							String subject = SystemMessageEnum.CONTRACT_DECLINED.getName(recipient.getLocale());
							Message m = new Message(PlayerMessageType.CONTRACT_DECLINED, proposer, recipient, subject, "");
							session.dropMail(recipient, m);
						}
						else if (p instanceof Alliance)
						{
							Alliance recipient = (Alliance) p;
							Message m = new Message(PlayerMessageType.CONTRACT_DECLINED, proposer, (Player) null, "", "");
							session.dropMail(recipient, m);
						}
					}
				}
			}
		});

	ActionHandler.add("ContractProposal.allianceAccept", new PostHandler()
	{
		@Override
		public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
		{
			int index = Integer.parseInt(req.getParameter("index"));
			ContractProposalList proposals = session.getGalaxy().getContractProposals();
			Player proposer = session.getPlayer();
			if (proposer.getAlliance() == null)
			{
				session.log(ErrorCode.CANNOT_MAKE_ALLIANCE_CONTRACT_NO_ALLIANCE);
				return;
			}
			if (!proposer.getRank().hasAllianceRight(RightEnum.MAKE_CONTRACTS))
			{
				session.log(ErrorCode.CANNOT_MAKE_ALLIANCE_CONTRACT_NO_RIGHT);
				return;				
			}
			Contract c = proposals.acceptProposal(session.getGalaxy().getDiplomaticRelation(),
				index, session.getAlliance(), session.getGalaxy().getWallClockTime());
			if (c != null)
			{
				List<ContractParty> parties = c.getParties();
				for (int i = 0; i < parties.size(); i++)
				{
					if (parties.get(i) instanceof Player)
					{
						Player recipient = (Player) parties.get(i);
						String subject = SystemMessageEnum.CONTRACT_CREATION.getName(recipient.getLocale());
						Message m = new Message(PlayerMessageType.CONTRACT_CREATION, proposer, recipient, subject, "");
						session.dropMail(recipient, m);
					}
					else if (parties.get(i) instanceof Alliance)
					{
						Alliance recipient = (Alliance) parties.get(i);
						Message m = new Message(PlayerMessageType.CONTRACT_CREATION, proposer, (Player) null, "", "");
						session.dropMail(recipient, m);
					}
				}
			}
		}
	});

	ActionHandler.add("ContractProposal.allianceDecline", new PostHandler()
	{
		@Override
		public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
		{
			int index = Integer.parseInt(req.getParameter("index"));
			ContractProposalList proposals = session.getGalaxy().getContractProposals();
			ContractProposal cp = proposals.get(index);
			Player proposer = session.getPlayer();
			if (proposer.getAlliance() == null)
			{
				session.log(ErrorCode.CANNOT_MAKE_ALLIANCE_CONTRACT_NO_ALLIANCE);
				return;
			}
			if (!proposer.getRank().hasAllianceRight(RightEnum.MAKE_CONTRACTS))
			{
				session.log(ErrorCode.CANNOT_MAKE_ALLIANCE_CONTRACT_NO_RIGHT);
				return;				
			}
			if (proposals.declineProposal(index, session.getAlliance(), session.getGalaxy().getWallClockTime()))
			{
				for (ContractParty p : cp)
				{
					if (p instanceof Player)
					{
						Player recipient = (Player) p;
						String subject = SystemMessageEnum.CONTRACT_DECLINED.getName(recipient.getLocale());
						Message m = new Message(PlayerMessageType.CONTRACT_DECLINED, proposer, recipient, subject, "");
						session.dropMail(recipient, m);
					}
					else if (p instanceof Alliance)
					{
						Alliance recipient = (Alliance) p;
						Message m = new Message(PlayerMessageType.CONTRACT_DECLINED, proposer, (Player) null, "", "");
						session.dropMail(recipient, m);
					}
				}
			}
		}
	});

	ActionHandler.add("ContractWarDraft.setAttacker", new PostHandler()
	{
		@Override
		public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
		{
			String attacker = req.getParameter("attacker");
			if ("p".equalsIgnoreCase(attacker))
			{
				session.setPlannedAttacker(true);
				if (session.getPlannedVictim() == session.getPlayer())
					session.setPlannedVictim(null);
			}
			else if ("a".equalsIgnoreCase(attacker))
			{
				if (session.getAlliance() == null)
				{
					session.setPlannedAttacker(true);
					session.log(ErrorCode.CANNOT_MAKE_ALLIANCE_CONTRACT_NO_ALLIANCE);
					return;
					
				}
				else if (!session.getPlayer().getRank().hasAllianceRight(RightEnum.MAKE_CONTRACTS))
				{
					session.setPlannedAttacker(true);
					session.log(ErrorCode.CANNOT_MAKE_ALLIANCE_CONTRACT_NO_RIGHT);
					return;
				}
				else
				{
					session.setPlannedAttacker(false);
					if (session.getPlannedVictim() == session.getPlayer().getAlliance())
						session.setPlannedVictim(null);
				}
			}
			else throw new IllegalArgumentException();
		}
	});

	ActionHandler.add("ContractWarDraft.setVictimPlayer", new PostHandler()
	{
		@Override
		public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
		{
			int index = Integer.parseInt(req.getParameter("index"));
			Player p = session.getGalaxy().findPlayerByPid(index);
			if ((session.getPlayer().getAlliance() == null) || !session.getPlayer().getRank().hasAllianceRight(RightEnum.MAKE_CONTRACTS))
				session.setPlannedAttacker(true);
			if (session.plannedAttackerIsPlayer() && (p == session.getPlayer()))
				return;
			session.setPlannedVictim(p);
		}
	});

	ActionHandler.add("ContractWarDraft.setVictimAlliance", new PostHandler()
	{
		@Override
		public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
		{
			int index = Integer.parseInt(req.getParameter("index"));
			Alliance a = session.getGalaxy().findAllianceById(index);
			if ((session.getPlayer().getAlliance() == null) || !session.getPlayer().getRank().hasAllianceRight(RightEnum.MAKE_CONTRACTS))
				session.setPlannedAttacker(true);
			if (!session.plannedAttackerIsPlayer() && (a == session.getPlayer().getAlliance()))
				return;
			session.setPlannedVictim(a);
		}
	});

	ActionHandler.add("ContractWarDraft.do", new PostHandler()
	{
		@Override
		public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
		{
			boolean isPlayer = session.plannedAttackerIsPlayer();
			ContractParty victim = session.getPlannedVictim();
			Player proposer = session.getPlayer();
			ContractParty attacker;
			
			if (isPlayer)
				attacker = proposer;
			else
			{
				attacker = proposer.getAlliance();
				if (proposer.getAlliance() == null)
				{
					session.log(ErrorCode.CANNOT_MAKE_ALLIANCE_CONTRACT_NO_ALLIANCE);
					return;
				}
				else if (!proposer.getRank().hasAllianceRight(RightEnum.MAKE_CONTRACTS))
				{
					session.log(ErrorCode.CANNOT_MAKE_ALLIANCE_CONTRACT_NO_RIGHT);
					return;
				}
			}
			CreateWarDeclaration war = new CreateWarDeclaration(
					session.getGalaxy().getDiplomaticRelation(), attacker, victim, session.getGalaxy().getTime());
			war.execute();
			// The victim immediately declares war on the attacker, too.
			war = new CreateWarDeclaration(
					session.getGalaxy().getDiplomaticRelation(), victim, attacker, session.getGalaxy().getTime());
			war.execute();
			if (victim instanceof Player)
			{
				Player recipient = (Player) victim;
				String subject = SystemMessageEnum.WAR_DECLARATION.getName(recipient.getLocale());
				Message m = new Message(PlayerMessageType.WAR_DECLARATION, proposer, recipient, subject, "");
				session.dropMail(recipient, m);
			}
			else if (victim instanceof Alliance)
			{
				Alliance recipient = (Alliance) victim;
				Message m = new Message(PlayerMessageType.WAR_DECLARATION, proposer, (Player) null, "", "");
				session.dropMail(recipient, m);
			}

			
			
		}
	});
	
	ActionHandler.add("Contract.playerLeaves", new PostHandler()
	{
		@Override
		public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
		{
			int index = Integer.parseInt(req.getParameter("index"));
			Player proposer = session.getPlayer();
			Contract c = proposer.getContracts().get(index);
			if ((c == null) || !c.contains(proposer)) return;
			
			if (c instanceof WarDeclaration)
			{
				WarDeclaration war = (WarDeclaration) c;
				if (proposer != war.getAttacker())
				{
					session.log(ErrorCode.CANNOT_LEAVE_WARDECLARATION_VICTIM);
					return;
				}
				List<ContractParty> parties = c.getParties();
				for (int i = 0; i < parties.size(); i++)
				{
					if (parties.get(i) instanceof Player)
					{
						Player recipient = (Player) parties.get(i);
						String subject = SystemMessageEnum.CANCEL_WAR_DECLARATION.getName(recipient.getLocale());
						Message m = new Message(PlayerMessageType.WAR_DECLARATION_CANCELLED, proposer, recipient, subject, "");
						session.dropMail(recipient, m);
					}
					else if (parties.get(i) instanceof Alliance)
					{
						Alliance recipient = (Alliance) parties.get(i);
						Message m = new Message(PlayerMessageType.WAR_DECLARATION_CANCELLED, proposer, (Player) null, "", "");
						session.dropMail(recipient, m);
					}
				}
				RemovePartyAction action = new RemovePartyAction(session.getGalaxy().getTime(),
							session.getGalaxy().getDiplomaticRelation(), war.getAttacker(), c);
				action.execute();
			}
			else
			{
				List<ContractParty> parties = c.getParties();
				for (int i = 0; i < parties.size(); i++)
				{
					if (parties.get(i) instanceof Player)
					{
						Player recipient = (Player) parties.get(i);
						String subject = SystemMessageEnum.LEAVE_CONTRACT.getName(recipient.getLocale());
						Message m = new Message(PlayerMessageType.CONTRACT_LEFT, proposer, recipient, subject, "");
						session.dropMail(recipient, m);
					}
					else if (parties.get(i) instanceof Alliance)
					{
						Alliance recipient = (Alliance) parties.get(i);
						Message m = new Message(PlayerMessageType.CONTRACT_LEFT, proposer, (Player) null, "", "");
						session.dropMail(recipient, m);
					}
				}
				RemovePartyAction action = new RemovePartyAction(
						session.getGalaxy().getTime(), session.getGalaxy().getDiplomaticRelation(), proposer, c);
				action.execute();
			}
		}
	});

	ActionHandler.add("Contract.allianceLeaves", new PostHandler()
	{
		@Override
		public void activate(ParsedRequest request, CqsSession session, HttpServletRequest req)
		{
			int index = Integer.parseInt(req.getParameter("index"));
			Player proposer = session.getPlayer();
			Alliance a = proposer.getAlliance();
			if (a == null)
			{
				session.log(ErrorCode.CANNOT_MAKE_ALLIANCE_CONTRACT_NO_ALLIANCE);
				return;
			}
			else if (!proposer.getRank().hasAllianceRight(RightEnum.MAKE_CONTRACTS))
			{
				session.log(ErrorCode.CANNOT_MAKE_ALLIANCE_CONTRACT_NO_RIGHT);
				return;
			}
			Contract c = a.getContracts().get(index);
			if ((c == null) || !c.contains(a)) return;
			
			if (c instanceof WarDeclaration)
			{
				WarDeclaration war = (WarDeclaration) c;
				if (a != war.getAttacker())
				{
					session.log(ErrorCode.CANNOT_LEAVE_WARDECLARATION_VICTIM);
					return;
				}
				List<ContractParty> parties = c.getParties();
				for (int i = 0; i < parties.size(); i++)
				{
					if (parties.get(i) instanceof Player)
					{
						Player recipient = (Player) parties.get(i);
						String subject = SystemMessageEnum.CANCEL_WAR_DECLARATION.getName(recipient.getLocale());
						Message m = new Message(PlayerMessageType.WAR_DECLARATION_CANCELLED, proposer, recipient, subject, "");
						session.dropMail(recipient, m);
					}
					else if (parties.get(i) instanceof Alliance)
					{
						Alliance recipient = (Alliance) parties.get(i);
						Message m = new Message(PlayerMessageType.WAR_DECLARATION_CANCELLED, proposer, (Player) null, "", "");
						session.dropMail(recipient, m);
					}
				}
				RemovePartyAction action = new RemovePartyAction(
						session.getGalaxy().getTime(), session.getGalaxy().getDiplomaticRelation(), war.getAttacker(), c);
				action.execute();
			}
			else
			{
				List<ContractParty> parties = c.getParties();
				for (int i = 0; i < parties.size(); i++)
				{
					if (parties.get(i) instanceof Player)
					{
						Player recipient = (Player) parties.get(i);
						String subject = SystemMessageEnum.LEAVE_CONTRACT.getName(recipient.getLocale());
						Message m = new Message(PlayerMessageType.CONTRACT_LEFT, proposer, recipient, subject, "");
						session.dropMail(recipient, m);
					}
					else if (parties.get(i) instanceof Alliance)
					{
						Alliance recipient = (Alliance) parties.get(i);
						Message m = new Message(PlayerMessageType.CONTRACT_LEFT, proposer, (Player) null, "", "");
						session.dropMail(recipient, m);
					}
				}
				RemovePartyAction action = new RemovePartyAction(
						session.getGalaxy().getTime(), session.getGalaxy().getDiplomaticRelation(), a, c);
				action.execute();
			}
		}
	});
}

private PostDiplomacy()
{/*OK*/}

}
