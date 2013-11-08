package utils;

public class UnsupportedFeatureException extends Exception
{
	/** A unique key for identifying something-or-other
	 */
	private static final long serialVersionUID = -4777584873012075850L;

	public static enum Type {
		SUBTLE_DATA__VOIDED_ITEMS,
		SUBTLE_DATA__PARTIALLY_PAID
	};
	private Type code;

	public UnsupportedFeatureException(Type code)
	{
		super();
		this.code = code;
	}
	public Type getType()
	{
		return code;
	}
	public String getMessage()
	{
		switch(code) {
			case SUBTLE_DATA__VOIDED_ITEMS: return "There are voided items on your ticket, which this app cannot handle";
			case SUBTLE_DATA__PARTIALLY_PAID: return "Part of your ticket has already been paid in some way that this app cannot understand";
			default: return "";
		}
	}
}
