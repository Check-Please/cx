Here we define the spec for the datatype `Ticket`, which represents a single
ticken in an order.

This spec uses the datatype `TicketItem`, which is defined in [`item.md`](
item.md).

JSON Format
===========

```js
{
	"id": int|String (optional),
	"seatNum": int (optional),
	"dateOpened": int (optional),
	"fee": int (optional),
	"tax": int (optional),
	"serviceCharge": int (optional),
	"discount": int (optional),
	"items": TicketItem[]
}
```

If `id` is a string, it must be universally unique.  If not, it must be at
least unique across all tickets on the order.  If it is not specified, the
index of this ticket in the `tickets` property of the order will be used.

The `dateOpened` should be the number of milliseconds since 1970.  While not
strictly required, it should be specified whenever possible.

Prices should be specified in hundreths of a cent to avoid rounding errors.

The `fee`, `tax`, `serviceCharge`, and `discount`, if specified, will be
divided up amoung the items in the ticket in proportion to how much each item
costs.  If not specified, they will default to `0`.  Keep in mind that these
are values which will be associated with the ticket as a whole, rather than
with any particular item.  If an item is on happy hour, for instance, it
would be more appropriate to associate the discount with the item which is
being discounted, rather than the ticket as a whole.

The `fee` property in particular should be used sparingly.
