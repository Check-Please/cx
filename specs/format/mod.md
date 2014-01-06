Here we define the spec for the datatype `ItemMod`, which represents a single
modifier on a ticket item.

JSON Format
===========

```js
{
	"name": String,
	"type": String (optional),
	"parentQuestion": String (optional),
	"price": int (optional),
	"tax": int (optional),
	"serviceCharge": int (optional),
	"discount": int (optional),
}
```

The `type` property is useful for analytics, but is not required.  If
specified, it should be universally unique.  If not, the `name` proerty can
be used, though this may not be reliable since names might change over time.

The `parentQuestion` property is also for analytics.  Basically, many
modifiers can be viewed as answers to questions (e.g. "Would you like fries
or a salad with that?").  This property, if specified, can tell the analyser
if two modifiers are both answers to the same question.

Prices should be specified in hundreths of a cent to avoid rounding errors.
The default value for prices is `0`.
