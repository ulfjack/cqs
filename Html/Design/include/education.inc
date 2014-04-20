<h3><a class="titlelink" href="colony-education|%= colony.getPosition;" onclick="return showEducation('%= colony.getPosition;')">%("education"); (%("{0} of {1} professors free", colony.getFreeProfessors, colony.getProfessors);)</a> <a href="colony-education|%= colony.getPosition;" onclick="return showEducation('%= colony.getPosition;')" id="colony-education-link">%("show details");</a></h3>

<script type="text/javascript" src="js/player-universities.js"></script>

<table class="stats statsform">
<colgroup><col width="60%%" /><col width="40%%" /></colgroup>
	<tr>
		<td><img src="%= Session.getUrl(ImageEnum.SYMBOL_EDUCATION);" alt="" width="20" height="20" /> %("{0,number,integer} professors for education", colony.getEducationSpaces);</td>
		<td><span id="edu-btns">
			(<a href="?Education.add=%= colony.getPosition;,-1" class="%= colony.maxRemoveEducationProfs > 0;">-1</a>
			<a href="?Education.add=%= colony.getPosition;,-2" class="%= colony.maxRemoveEducationProfs > 1;">-2</a>
			<a href="?Education.add=%= colony.getPosition;,-5" class="%= colony.maxRemoveEducationProfs > 4;">-5</a>
			<a href="javascript:removeNedu('?Education.add=%= colony.getPosition;,')" class="%= colony.maxRemoveEducationProfs > 2;">-N</a>) &#160;
			<a href="?Education.add=%= colony.getPosition;,1" class="%= colony.getFreeProfessors > 0;">+1</a>
			<a href="?Education.add=%= colony.getPosition;,2" class="%= colony.getFreeProfessors > 1;">+2</a>
			<a href="?Education.add=%= colony.getPosition;,5" class="%= colony.getFreeProfessors > 4;">+5</a>
			<a href="javascript:addNedu('?Education.add=%= colony.getPosition;,')" class="%= colony.getFreeProfessors > 2;">+N</a>
		</span></td>
	</tr>
	<tr>
		<td><img src="%= Session.getUrl(ImageEnum.SYMBOL_RESEARCH);" alt="" width="20" height="20" /> <a href="research|current">%("{0,number,integer} professors for research", colony.getResearchSpaces);</a></td>
		<td><span id="research-btns">
			(<a href="?Research.add=%= colony.getPosition;,-1" class="%= colony.getResearchSpaces > 0;">-1</a>
			<a href="?Research.add=%= colony.getPosition;,-2" class="%= colony.getResearchSpaces > 1;">-2</a>
			<a href="?Research.add=%= colony.getPosition;,-5" class="%= colony.getResearchSpaces > 4;">-5</a>
			<a href="javascript:removeNres('?Research.add=%= colony.getPosition;,')" class="%= colony.getResearchSpaces > 2;">-N</a>) &#160;
			<a href="?Research.add=%= colony.getPosition;,1" class="%= colony.getFreeProfessors > 0;">+1</a>
			<a href="?Research.add=%= colony.getPosition;,2" class="%= colony.getFreeProfessors > 1;">+2</a>
			<a href="?Research.add=%= colony.getPosition;,5" class="%= colony.getFreeProfessors > 4;">+5</a>
			<a href="javascript:addNres('?Research.add=%= colony.getPosition;,')" class="%= colony.getFreeProfessors > 2;">+N</a>
		</span></td>
	</tr>
</table>
