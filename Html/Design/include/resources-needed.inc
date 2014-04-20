%loop y from 1 to 4 where Building.needs(x, y) > 0;
  %if Ressource.amount(y) < Building.needs(x, y);
    <i class="a">
    <img src="n/res%= y;.png" height="14" width="14" alt="" />
    %("{0,number,integer}", Building.needs(x,y));</i>
  %else;
    <img src="n/res%= y;.png" height="14" width="14" alt="" />
    %("{0,number,integer}", Building.needs(x,y));
  %endif;
  &#160;
%endloop;

%if Building.people(x) > 0;
  %if Population.amount < Building.workers(x)+Building.people(x);
    <i class="a">%("constructors + crew: {0,number,integer} + {1,number,integer}", Building.workers(x), Building.people(x));</i>
  %else;
    %("constructors + crew: {0,number,integer} + {1,number,integer}", Building.workers(x), Building.people(x));
  %endif; 
%else;
  %if Building.workers(x) > 0;
    %if Population.amount < Building.workers(x);
      <i class="a">%("population: {0,number,integer}", Building.workers(x));</i>
    %else;
      %("population: {0,number,integer}", Building.workers(x));
    %endif; 
  %endif;
%endif;

