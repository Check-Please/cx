package utils;

public class UnsupportedFeatureException extends HttpErrMsg
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
		super(UnsupportedFeatureException.getCode(code), UnsupportedFeatureException.getMessage(code));
		this.code = code;
	}
	public Type getType()
	{
		return code;
	}
	private static int getCode(Type code)
	{
		return 404;
	}
	private static String getMessage(Type code)
	{
		switch(code) {
			case SUBTLE_DATA__VOIDED_ITEMS: return "There are voided items on your ticket, which this app cannot handle";
			case SUBTLE_DATA__PARTIALLY_PAID: return "Part of your ticket has already been paid in some way that this app cannot understand";
			default: return "";
		}
	}
}
