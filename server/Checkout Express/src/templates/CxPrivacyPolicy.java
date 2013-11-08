package templates;

public class CxPrivacyPolicy {
	public static String run() {
		return	"<p>This privacy policy sets out how Checkout Express uses and protects any information that you give Checkout Express when you use this website.</p>"+
			"<p>Checkout Express is committed to ensuring that your privacy is protected. Should we ask you to provide certain information by which you can be identified when using this website, then you can be assured that it will only be used in accordance with this privacy statement.</p>"+
			"<p>Checkout Express may change this policy from time to time by updating this page. You should check this page from time to time to ensure that you are happy with any changes. This policy is effective from July 23, 2013.</p>"+
			""+
			"<h1>What we collect and how we use it</h1>"+
			"<p>We collect the following information:</p>"+
			"<ul>"+
			"\t<li>Email Address</li>"+
			"\t<li>Encrypted Passwords</li>"+
			"\t<li>Credit Card Data</li>"+
			"\t<li>Ratings Information</li>"+
			"\t<li>Usage Data</li>"+
			"</ul>"+
			"<p>The email address is used primarily as a username. However, it can be used by a user in order to reset their password if they forgot it</p>"+
			"<p>The encrypted passwords are used exclusively during the login process in order to make sure that a user has entered the correct password.  All passwords are SHA256 encrypted and are very difficult (practically impossible) to decrypt.  No unencrypted passwords are stored</p>"+
			"<p>The details of credit card data storage and usage are described in the next section</p>"+
			"<p>When you finish paying, you will be asked to rate your experience.  This ratings information is currently not stored and nothing is done with it by the server</p>"+
			"<p>\"'Usage data\"' is a generic terms referring to information about how you use the app (e.g. if you give up part way through using the app).  This is generally information which you do not explicitly enter into any forms and is collected automatically.  This data is generally used in order to improve your user experience</p>"+
			""+
			"<h1>Credit Card Data and Processing</h1>"+
			"<p>We store the following information about a credit card on our servers:</p>"+
			"<ul>"+
			"\t<li>Issuer Identification Number (IIN)</li>"+
			"\t<li>The last four digits of the Primary Account Number (PAN)</li>"+
			"\t<li>The name on the card</li>"+
			"\t<li>The expiration date of the card</li>"+
			"\t<li>The zip code associated with the card</li>"+
			"</ul>"+
			"<p>Not that we do not store the full PAN nor the Card Security Code (CSC) on our servers.  We also do everything we can in order to keep the information we do store secure, including using TSL/SSL encryption and only using the minimal amount of information necessary at any given time</p>"+
			"<p>That said, additional information is stored by SubtleData, Inc.  SubtleData is also in charge of handling the actual credit card payments made by this application.  Currently, SubtleData does so by running credit card information through the restaurant\'s Point of Sale (POS) software.  As a result, credit cards payments made by this application should appear as though they were made by the POS software of the restaurant, which is the usual means of charging credit cards</p>"+
			""+
			"<h1>How we use cookies</h1>"+
			"<p>A cookie is a small file on your computer\'s hard drive.  We use cookies in order to keep you logged into our service even when you navigate to another page</p>"+
			""+
			"<h1>Controlling your personal information</h1>"+
			"<p>You may request details of personal information which we hold about you under the Data Protection Act 1998. A small fee will be payable. If you would like a copy of the information held on you please write to Martin Jelin, 1091 Morewood Ave., Pittsburgh, PA, 15213, or email sjelin@chkex.com</p>"+
			"<p>If you believe that any information we are holding on you is incorrect or incomplete, please write to or email us as soon as possible, at the above address. We will promptly correct any information found to be incorrect.</p>";
	}
}