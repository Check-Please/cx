package modeltypes;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import utils.DSConverter;
import utils.HttpErrMsg;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.ShortBlob;

import org.apache.commons.codec.binary.Base64;

public class Client extends AbstractModelType
{
	protected String kindName() { return getKind(); }
	public static String getKind() { return "client"; }
	List<String> connectionIDs;
	byte [] key;
	public Client(Key k, DatastoreService ds) throws EntityNotFoundException { super(k, ds); }
	public Client(Entity e) { super(e); }
	public Client(Key k)
	{
		setKey(k);
		connectionIDs = new ArrayList<String>(1);
		key = null;
	}

	public void logConnection(String cID)
	{
		connectionIDs.add(cID);
	}
	
	/**	Decrypts ciphertext using AES and the key associated with the client
	 *	@param ivAndCt The IV and Ciphertext
	 *	@return The plaintext
	 *	@throws HttpErrMsg if something goes wrong.  May be a 500 error or 404
	 */
	public String decrypt(String ivAndCt) throws HttpErrMsg
	{
		if(key == null)
			throw new HttpErrMsg(404, "No encryption key on hand");
		String [] tokens = ivAndCt.split(" ");
		IvParameterSpec iv;
		byte [] ct;
		try {
			if(tokens.length != 2)
				throw new Exception();
			iv = new IvParameterSpec(Base64.decodeBase64(tokens[0].getBytes()));
			ct = Base64.decodeBase64(tokens[1].getBytes());
		} catch(Exception e) {
			throw new HttpErrMsg(404, "Encrypted credit card information was malformated");
		}
		SecretKey s;
	    try {
	    	s = new SecretKeySpec(key, 0, key.length, "AES");
	    } catch(Exception ex) {
	    	throw new HttpErrMsg(500, "Could not load encryption key");
	    }
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (Exception e) {
	    	throw new HttpErrMsg(500, "Could not load cipher");
		}
		try {
			cipher.init(Cipher.DECRYPT_MODE, s, iv);
		} catch (InvalidKeyException e) {
	    	throw new HttpErrMsg(500, "Encryption key corrupted");
		} catch (InvalidAlgorithmParameterException e) {
			throw new HttpErrMsg(404, "IV malformated");
		}
		try {
			return new String(cipher.doFinal(ct));
		} catch (Exception e) {
			throw new HttpErrMsg(404, "Ciphertext malformated");
		}
	}
	
	/**	Encrypts text using AES and the key associated with the client
	 *	@param plaintext string
	 *	@return The The IV and Ciphertext
	 *	@throws HttpErrMsg if something goes wrong.  May be a 500 error or 404
	 */
	public String encrypt(String plaintext) throws HttpErrMsg
	{
		if(key == null)
			throw new HttpErrMsg(404, "No encryption key on hand");
	    try {
	    	SecretKey s = new SecretKeySpec(key, 0, key.length, "AES");
	    	Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    	cipher.init(Cipher.ENCRYPT_MODE, s);
	    	AlgorithmParameters params = cipher.getParameters();
	    	return	new String(Base64.encodeBase64(params.getParameterSpec(IvParameterSpec.class).getIV()))+" "+
	    			new String(Base64.encodeBase64(cipher.doFinal(plaintext.getBytes())));
	    } catch(Exception ex) {
	    	throw new HttpErrMsg(500, "Could not encrypt data");
	    }
	}

	public boolean hasPrivateKey()
	{
		return key != null;
	}

	public void setKey() throws HttpErrMsg
	{
		KeyGenerator keyGen;
		try {
			keyGen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			throw new HttpErrMsg(500, "Could not generate encryption key");
		}
		keyGen.init(128); // for example
		key = keyGen.generateKey().getEncoded();
	}

	public Entity toEntity()
	{
		Entity e = new Entity(getKey());
		DSConverter.setList(e, "connectionIDs", connectionIDs);
		if(key != null) {
			e.setProperty("rsaMod", new ShortBlob(key));
		}
		return e;
	}
	public void fromEntity(Entity e)
	{
		connectionIDs = DSConverter.getList(e, "connectionIDs");
		if(e.hasProperty("rsaMod")) {
			key = ((ShortBlob) e.getProperty("rsaMod")).getBytes();
		} else
			key = null;
	}
}

