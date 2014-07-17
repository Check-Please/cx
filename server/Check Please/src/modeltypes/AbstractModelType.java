package modeltypes;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;

public abstract class AbstractModelType
{
	private Key key;//The entity's key in the datastore
	protected abstract String kindName();//Returns the name of the kind of the entity

	/**	Default constructor.  All it does is set key to null
	 */
	public AbstractModelType() {key = null;};

	/**	Loads an entity from the datastore.
	 *
	 *	@param	symKey	The key of the entity
	 *	@param	ds	The datastore to load from
	 *
	 *	@throws	EntityNotFoundException	Thrown the datastore has nothing to load
	 */
	public AbstractModelType(Key k, DatastoreService ds) throws EntityNotFoundException
	{
		setKey(k);
		if(!reload(ds))
			throw new EntityNotFoundException(k);
	}

	/**	Wraps an entity
	 *
	 *	@param e	The entity to wrap
	 *	@param ds	The datastore to add to/load from
	 */
	public AbstractModelType(Entity e)
	{
		setKey(e.getKey());
		fromEntity(e);
	}

	/**	Unwraps the object into google's type of entity
	 *
	 *	For any wrapped entity x, it must always be true that to code:
	 *		x.fromEntity(x.toEntity())
	 *	has no effect
	 *
	 *	@return	An unwrapped version of an entity
	 */
	public abstract Entity toEntity();

	/**	Loads the value's of google's type of entity into this wrapped entity
	 *
	 *	For any wrapped entity x, it must always be true that to code:
	 *		x.fromEntity(x.toEntity())
	 *	has no effect
	 *
	 *	@param	e	The entity to load values from.  Must be of the correct kind
	 */
	public abstract void fromEntity(Entity e);

	/**	Gets the key of the entity
	 * 
	 *	@return	The key of the entity
	 */
	public Key getKey()
	{
		return key;
	}

	private Key restrKey = null;//For memoizing
	/** Gets the key for the restaurant which the entity belongs to
	 *
	 *	@return The restaurant key
	 */
	protected Key getRestrKey()
	{
		if(restrKey == null) {
			Key pKey;
			restrKey = key;
			while((pKey = restrKey.getParent()) != null)
				restrKey = pKey;
		}
		return restrKey;
	}

	/**	Sets the key of the entity
	 *	@param k The key of the entity
	 */
	protected void setKey(Key k)
	{
		if(!k.getKind().equals(kindName()))
			throw new IllegalArgumentException();
		key = k;
	}

	/**	Overwrites whatever is currently in the entity with what the
	 *	datastore's version
	 *
	 *	@param	ds	The datastore to load from
	 *	@return	True iff there was an entity in the datastore to load
	 */
	public boolean reload(DatastoreService ds)
	{
		try {
			fromEntity(ds.get(getKey()));
			return true;
		} catch (EntityNotFoundException e) {
			return false;
		}
	}

	/**	Writes the entity into the datastore
	 * 
	 *	@param	ds	The datastore to commit to
	 */
	public final void commit(DatastoreService ds)
	{
		ds.put(toEntity());
	}

	/**	Removes an entity from the datastore
	 * 
	 *	@param	ds	The datastore to commit to
	 */
	public void rmv(DatastoreService ds)
	{
		ds.delete(getKey());
	}
}
