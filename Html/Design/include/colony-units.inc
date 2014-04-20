%set uqueue colony.getUnitQueue(q);
%set building q = 0 ? Building.MILITARY_BASE : (q = 1 ? Building.SHIPYARD : Building.SPACEPORT);
%if colony.getBuilding(building) = 0;

	<div class="title" id="bql%= q;">%("No {0} built", building.getName(locale));</div>

%else;

	%set action Request.getParameter("action");
	%set tmpCount Request.getParameter("count");
	%if (action="addUnit") & (tmpCount = "N");
	%set tmpSlot UnitEnum.valueOf(Request.getParameter("unit"));
	%set tmpQ Tools.safeParseInt(Request.getParameter("queue"),-1);
	%if (tmpQ = q);
	<form action="?" method="post">
		<div>
			<input type="hidden" name="do" value="Colony.addUnit" />
			<input type="hidden" name="c" value="%= colony.getPosition;" />
			<input type="hidden" name="q" value="%= tmpQ;" />
			<input type="hidden" name="id" value="%= tmpSlot;" />
		</div>
		<fieldset class="stdform"><legend>%= tmpSlot.getName(locale);</legend>
	%loop url foreach Session.getUrls(tmpSlot, false);
			<div style="position:absolute">
				<img class="icon" src="%= url;" alt="" width="32" />
			</div>
	%endloop;
		<div style="margin-left: 34px;">
			%= tmpSlot.getName(locale);
			<input type="text" name="count" value="" />
			<button type="submit">%("build");</button>
		</div>
		</fieldset>
	</form>
	%endif;
	%endif;

	<div class="jobqueue" id="unitqueue-%= q;">
	%include "unit-queue.inc";
	</div>
	
	<table class="joblist">

	%set pdata Session.getPlayerDataCopy;
	%loop slot foreach pdata.getUnitDesigns where slot != null;
	%include "../include/unit-list-1.inc";
	%endloop;
	
	%loop slot foreach Galaxy.getUnitSystem.getUniqueUnits;
	%include "../include/unit-list-1.inc";
	%endloop;
	
	</table> <!-- joblist -->
	
	<div class="final"></div>

%endif;

