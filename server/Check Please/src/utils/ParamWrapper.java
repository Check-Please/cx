package utils;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public interface ParamWrapper
{
	public String getStr(int i);
	public List<String> getStrList(int i);
	public List<List<String>> getStr2DList(int i);

	public Long getLong(int i);
	public List<Long> getLongList(int i);
	public List<List<Long>> getLong2DList(int i);

	public Double getDouble(int i);
	public List<Double> getDoubleList(int i);
	public List<List<Double>> getDouble2DList(int i);

	public Boolean getBool(int i);
	public List<Boolean> getBoolList(int i);
	public List<List<Boolean>> getBool2DList(int i);

	public Long getKeyID(int i);
	public List<Long> getKeyIDList(int i);
	public List<List<Long>> getKeyID2DList(int i);

	public String getKeyName(int i);
	public List<String> getKeyNameList(int i);
	public List<List<String>> getKeyName2DList(int i);
	public Key getKey();
	public Key getKey(int i);
	public Entity getEntity();
	public Entity getEntity(int i);

	public String getQueryString();
	public String getChannelID() throws IOException;
	public String getPath();

	public Cookie[] getCookies();
	public void saveCookie(String name, String value, String path, Date expiry);
}
