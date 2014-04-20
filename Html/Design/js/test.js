CurryTest = TestCase("CurryTest");

function fnoargs(a)
{ return 20; }

function fonearg(a)
{ return a+10; }

function ftwoargs(a, b)
{ return a+b; }

CurryTest.prototype.testCurrySingleArgument = function() {
  var f = curry(fnoargs);
  assertEquals(20, f());
};

CurryTest.prototype.testCurrySingleArgument = function() {
  var f = curry(fonearg, null, 10);
  assertEquals(20, f());
};

CurryTest.prototype.testCurryTwoArgumentsUncurried = function() {
  var f = curry(ftwoargs);
  assertEquals(20, f(11, 9));
};

CurryTest.prototype.testCurryTwoArguments = function() {
  var f = curry(ftwoargs, null, 10);
  assertEquals(20, f(10));
};

CurryTest.prototype.testCurryTwoArgumentsAllCurried = function() {
  var f = curry(ftwoargs, null, 9, 11);
  assertEquals(20, f());
};

CurryTest.prototype._fnoarg = function() {
	return 20;
};

CurryTest.prototype._fonearg = function(a) {
	return 10+a;
};

CurryTest.prototype._ftwoargs = function(a, b) {
	return a+b;
};

CurryTest.prototype.testCurryObjectFunctionNoArguments = function() {
  var f = curry(this._fnoarg, this);
  assertEquals(20, f());
};

CurryTest.prototype.testCurryObjectFunctionUncurried = function() {
  var f = curry(this._fonearg, this);
  assertEquals(20, f(10));
};

CurryTest.prototype.testCurryObjectFunctionCurried = function() {
  var f = curry(this._fonearg, this, 10);
  assertEquals(20, f());
};

CurryTest.prototype.testCurryObjectFunctionTwoArgsCurried = function() {
  var f = curry(this._ftwoargs, this, 9);
  assertEquals(20, f(11));
};
