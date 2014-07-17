package modeltypes;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import utils.DSConverter;
import utils.HttpErrMsg;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.ShortBlob;

public class Client extends AbstractModelType
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "client"; }
	List<String> connectionIDs;
	byte [] symKey;
	byte [] authKey;
	public Client(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public Client(Entity e) { super(e); }
	public Client(Key k)
	{
		setKey(k);
		connectionIDs = new ArrayList<String>(1);
		symKey = null;
	}

	public void logConnection(String cID)
	{
		connectionIDs.add(cID);
	}
	
	/**	Decrypts ciphertext using AES and the key associated with the client
	 *	@param	digestIVAndEncMsgBase64 The auth digest, IV, and Ciphertext
	 *	@return The plaintext or null if the key does not match the ciphertext
	 *	@throws HttpErrMsg if something goes wrong.  May be a 500 error or 404
	 */
	public String decrypt(final String digestIVAndEncMsgBase64) throws HttpErrMsg
	{
		if(!this.hasPrivateKey())
			throw new HttpErrMsg(404, "No encryption key on hand");

		final SecretKey symK = new SecretKeySpec(symKey, 0, symKey.length, "AES");
		final SecretKey authK = new SecretKeySpec(authKey, 0, authKey.length, "HmacSHA256");
		final byte[] digestIVAndEncMsg = DatatypeConverter.parseBase64Binary(digestIVAndEncMsgBase64);

		try {
			final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			//Separate digest from remainder
			Mac mac = Mac.getInstance(authK.getAlgorithm());
			try {
				mac.init(authK);
			} catch (InvalidKeyException e) {
				throw new HttpErrMsg(500, "Invalid MAC key");
			}
			final byte [] digest = new byte[mac.getMacLength()];
			if(digest.length > digestIVAndEncMsg.length)
				throw new HttpErrMsg(404, "Couldn't decrypt card (ciphertext length incorrect)");
			System.arraycopy(digestIVAndEncMsg, 0, digest, 0, digest.length);
			final byte[] ivAndEncMsg = new byte[digestIVAndEncMsg.length - digest.length];
			System.arraycopy(digestIVAndEncMsg, digest.length, ivAndEncMsg, 0, ivAndEncMsg.length);

			//Check Digest
			if(!Arrays.equals(digest, mac.doFinal(ivAndEncMsg)))
				return null;

			//Separate IV from encoded message
			final byte[] ivData = new byte[cipher.getBlockSize()];
			if(ivData.length > ivAndEncMsg.length)
				throw new HttpErrMsg(404, "Couldn't decrypt card (ciphertext length incorrect)");
			System.arraycopy(ivAndEncMsg, 0, ivData, 0, ivData.length);
			final IvParameterSpec iv = new IvParameterSpec(ivData);
			final byte[] endMsg = new byte[ivAndEncMsg.length - ivData.length];
			System.arraycopy(ivAndEncMsg, ivData.length, endMsg, 0, endMsg.length);

			//Decrypt
			try {
				cipher.init(Cipher.DECRYPT_MODE, symK, iv);
			} catch (InvalidKeyException e) {
				throw new HttpErrMsg(500, "Invalid encryption key");
			} catch (InvalidAlgorithmParameterException e) {
				throw new HttpErrMsg(404, "Couldn't decrypt card (IV malformated)");
			}
			final byte[] plaintext;
			try {
				plaintext = cipher.doFinal(endMsg);
			} catch (BadPaddingException e) {
				return null;
			}
			return new String(plaintext, Charset.forName("UTF-8"));
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Unexpected exception during decryption", e);
		}
	}

	/**	Encrypts text using AES and the key associated with the client
	 *	@param	plaintext string
	 *	@return The The IV and Ciphertext
	 *	@throws HttpErrMsg if something goes wrong.  May be a 500 error or 404
	 */
	public String encrypt(final String plaintext) throws HttpErrMsg
	{
		if(!this.hasPrivateKey())
			throw new HttpErrMsg(404, "No encryption key on hand");

		final SecretKey symK = new SecretKeySpec(symKey, 0, symKey.length, "AES");
		final SecretKey authK = new SecretKeySpec(authKey, 0, authKey.length, "HmacSHA256");
		final byte[] encodedMessage = plaintext.getBytes(Charset.forName("UTF-8"));

		try {
			final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			//Generate random IV using block size (possibly create a method for this)
			final byte[] ivData = new byte[cipher.getBlockSize()];
			SecureRandom.getInstance("SHA1PRNG").nextBytes(ivData);
			final IvParameterSpec iv = new IvParameterSpec(ivData);

			//Encrypt
			try {
				cipher.init(Cipher.ENCRYPT_MODE, symK, iv);
			} catch (InvalidKeyException e) {
				throw new HttpErrMsg(500, "Invalid encryption key");
			}
			final byte[] encMsg = cipher.doFinal(encodedMessage);

			//Concatenate IV and encrypted message
			final byte[] ivAndEncMsg = new byte[ivData.length + encMsg.length];
			System.arraycopy(ivData, 0, ivAndEncMsg, 0, ivData.length);
			System.arraycopy(encMsg, 0, ivAndEncMsg, ivData.length, encMsg.length);

			//Make auth digest
			Mac mac = Mac.getInstance(authK.getAlgorithm());
			try {
				mac.init(authK);
			} catch (InvalidKeyException e) {
				throw new HttpErrMsg(500, "Invalid MAC key");
			}
			final byte [] digest = mac.doFinal(ivAndEncMsg);

			//Put everything together
			final byte[] digestIVAndEncMsg = new byte[digest.length + ivAndEncMsg.length];
			System.arraycopy(digest, 0, digestIVAndEncMsg, 0, digest.length);
			System.arraycopy(ivAndEncMsg, 0, digestIVAndEncMsg, digest.length, ivAndEncMsg.length);

			return DatatypeConverter.printBase64Binary(digestIVAndEncMsg);
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Unexpected exception during encryption", e);
		}
	}

	public boolean hasPrivateKey()
	{
		return (symKey != null) && (authKey != null);
	}

	public void setKey() throws HttpErrMsg
	{
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128);
			symKey = keyGen.generateKey().getEncoded();
			authKey = KeyGenerator.getInstance("HmacSHA256").generateKey().getEncoded();
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Unexpected exception during key generation", e);
		}
	}

	public Entity toEntity()
	{
		Entity e = new Entity(getKey());
		DSConverter.setList(e, "connectionIDs", connectionIDs);
		if(hasPrivateKey()) {
			e.setProperty("symKey", new ShortBlob(symKey));
			e.setProperty("authKey", new ShortBlob(authKey));
		}
		return e;
	}
	public void fromEntity(Entity e)
	{
		connectionIDs = DSConverter.getList(e, "connectionIDs");
		if(e.hasProperty("symKey") && e.hasProperty("authKey")) {
			symKey = ((ShortBlob) e.getProperty("symKey")).getBytes();
			authKey = ((ShortBlob) e.getProperty("authKey")).getBytes();
		} else {
			symKey = null;
			authKey = null;
		}
	}
}

