				<table>
				<colgroup><col width="25%%" span="4" /></colgroup>
					<tr>
					%loop x from 0 to 3;
						<td>
							<table>
							<colgroup><col width="40px" /><col width="*" /></colgroup>
								<tr>
									<td colspan="2"><b><span id="res_%= x;">%= Tools.getAbbreviatedLong(Session.getLocale, colony.getDisplayResource(x));</span> %= Resource.getName(locale,x);</b><br />%if colony.getDisplayResource(x) = colony.getStorage(x);<span class="red">%("depot full!");</span>%else;%("max {0}", Tools.getAbbreviatedLong(Session.getLocale, colony.getStorage(x)));%endif;</td></tr><tr>
									<td class="nowrap" title="%= Resource.getName(locale,x); [%("available: {0,number,integer}", colony.getDisplayResource(x));] [%("growth: +{0,number,integer}/h", colony.getRate(x));] [%("depot: {0,number,integer}", colony.getStorage(x));] [%("bonus: {0}%", colony.getModifier(x));]">
										<img src="%= Session.getFillLevelUrl(colony.getDisplayResource(x), colony.getStorage(x));" width="5" height="30" alt="" />
										<img src="%= Session.getUrl(Resource.get(x));" width="30" height="30" alt="%= Resource.getName(locale,x); [%("available: {0,number,integer}", colony.getDisplayResource(x));] [%("growth: +{0,number,integer}/h", colony.getRate(x));] [%("depot: {0,number,integer}", colony.getStorage(x));] [%("bonus: {0}%", colony.getModifier(x));]" />
									</td>
									
									<td title="%= Resource.getName(locale,x); [%("available: {0,number,integer}", colony.getDisplayResource(x));] [%("growth: +{0,number,integer}/h", colony.getRate(x));] [%("depot: {0,number,integer}", colony.getStorage(x));] [%("bonus: {0}%", colony.getModifier(x));]">
										<i>%("+{0}/h", Tools.getAbbreviatedLong(Session.getLocale, colony.getRate(x)));</i> %if Galaxy.getSpeed > 1;<br />(<i>%("+{0}/h", Tools.getAbbreviatedLong(Session.getLocale, colony.getRate(x)*Galaxy.getSpeed));</i>)%endif;<br />
									%("bonus: {0}%", colony.getModifier(x));
									</td>
								</tr>
							</table>
						</td>
					%endloop;
					</tr>
				</table>

