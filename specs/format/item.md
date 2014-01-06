Here we define the spec for the datatype `TicketItem`, which represents a
single item on a ticket.

This spec uses the datatype `ItemMod`, which is defined in [`mod.md`](mod.md
).

JSON Format
===========

```js
{
	"id": int|String (optional),
	"name": String,
	"type": String (optional),
	"dateAdded": int (optional),
	"price": int,
	"tax": int,
	"serviceCharge": int (optional),
	"discount": int (optional),
	"quantity": int (optional),
	"mods": ItemMod[]
}
```

If `id` is a string, it must be universally unique.  If not, it must be at
least unique across all tickets on the order.  If it is not specified, the
index of this item in the `items` property of the ticket will be used.

The `dateAdded` should be the number of milliseconds since 1970.  While not
strictly required, it should be specified whenever possible.

The `type` property is useful for analytics, but is not required.  If
specified, it should be universally unique.  If not, the `name` proerty can
be used, though this may not be reliable since names might change over time.

Prices should be specified in hundreths of a cent to avoid rounding errors.
The default value for prices is `0`.

THe default quantity is `1`.
