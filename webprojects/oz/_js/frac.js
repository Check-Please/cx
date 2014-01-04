function Frac(n, d) {
	if(n instanceof Frac) {
		this.n = n.n;
		this.d = n.d
	} else {
		this.n = n;
		this.d = d || 1;
		this.reduce();
	}
}

Frac.prototype.reduce = function() {
	var gcd = Math.gcd(this.n, this.d);
	this.n /= gcd;
	this.d /= gcd;
	if(this.d < 0) {
		this.n = -this.n;
		this.d = -this.d;
	}
}

Frac.prototype.add = function(n, d) {
	var that = new Frac(n, d);
	this.n = this.n*that.d+that.n*this.d;
	this.d = this.d*that.d;
	this.reduce();
}

Frac.prototype.sub = function(n, d) {
	var that = new Frac(n, d);
	this.n = this.n*that.d-that.n*this.d;
	this.d = this.d*that.d;
	this.reduce();
}

Frac.prototype.mult = function(n, d) {
	var that = new Frac(n, d);
	this.n = this.n*that.n;
	this.d = this.d*that.d;
	this.reduce();
}

Frac.prototype.div = function(n, d) {
	var that = new Frac(n, d);
	this.n = this.n*that.d;
	this.d = this.d*that.n;
	this.reduce();
}
