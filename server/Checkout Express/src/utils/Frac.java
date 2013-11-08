package utils;

import java.util.ArrayList;
import java.util.List;

public class Frac implements Comparable<Frac>
{
	public static final Frac ONE = new Frac(1L, 1L);
	public static final Frac ZERO = new Frac(0L, 1L);

	private final long n;
	private final long d;

	public Frac(long n,  long d)
	{
		if(d == 0L)
			throw new IllegalArgumentException("Cannot divide by zero");
		//Reduce
		long a;
		long b;
		long gcd = 0L;
		if(d < 0L) {
			n *= -1L;
			d *= -1L;
		}
		if(n == d)
			n = d = 1L;
		else if(n == 0L)
			d = 1L;
		else {
			if(n > d) {
				a = n;
				b = d;
			} else {
				a = d;
				b = n;
			}
			while(gcd == 0L) {
				a %= b;
				if(a == 0L)
					gcd = b;
				else {
					b %= a;
					if(b == 0L)
						gcd = a;
				}
			}
			n /= gcd;
			d /= gcd;
		}

		this.n = n;
		this.d = d;
	}
	
	public long getNum()
	{
		return n;
	}
	
	public long getDenom()
	{
		return d;
	}
	
	public double getVal()
	{
		return n*1.0/d;
	}

	public Frac add(long n, long d)
	{
		return new Frac(this.n*d+this.d*n, this.d*d);
	}

	public Frac sub(long n, long d)
	{
		return new Frac(this.n*d-this.d*n, this.d*d);
	}

	public Frac mult(long n, long d)
	{
		return new Frac(this.n*n, this.d*d);
	}

	public Frac div(long n, long d)
	{
		return new Frac(this.n*d, this.d*n);
	}

	public Frac add(Frac f)
	{
		return this.add(f.n, f.d);
	}

	public Frac sub(Frac f)
	{
		return this.sub(f.n, f.d);
	}

	public Frac mult(Frac f)
	{
		return this.mult(f.n, f.d);
	}

	public Frac div(Frac f)
	{
		return this.div(f.n, f.d);
	}

	public Frac add(long n)
	{
		return this.add(n, 1L);
	}

	public Frac sub(long n)
	{
		return this.sub(n, 1L);
	}

	public Frac mult(long n)
	{
		return this.mult(n, 1L);
	}

	public Frac div(long n)
	{
		return this.div(n, 1L);
	}

	public long floor()
	{
		return n/d;
	}

	public long round()
	{
		return (n + (d + (d%2))/2) / d;
	}

	public long ceil()
	{
		return (n+d-1)/d;
	}

	public int compareTo(Frac f)
	{
		long diff = n*f.d - d*f.n;
		if(diff == 0L)
			return 0;
		else if(diff < 0L)
			return -1;
		else
			return 1;
	}

	public int hashCode()
	{
		return (int)(n+31*d);
	}

	public boolean equals(Object o)
	{
		if(o instanceof Frac) {
			Frac f = (Frac) o;
			return n == f.getNum() && d == f.getDenom();
		} else
			return false;
	}

	public String toString()
	{
		return n+"/"+d;
	}
	
	public static List<Frac> makeFracs(List<Long> ns, List<Long> ds)
	{
		if(ns.size() != ds.size())
			throw new IllegalArgumentException("Numerator and denominator list lengths do not match");
		List<Frac> fs = new ArrayList<Frac>(ns.size());
		for(int i = 0; i < ns.size(); i++)
			fs.add(new Frac(ns.get(i), ds.get(i)));
		return fs;
	}
}
