/*
	Cae's Auto Line Discount

	uses product properties:
		AutoLineDiscount
			(integer 0-100 as % to discount)

		AutoLineDiscount_Expiry
			(time in milliseconds, standard Java format,
			 at which this discount will no longer apply)
			TODO: infinite expiry? (just use far into future for now..)
*/

// event.change:

import com.openbravo.format.Formats;
import com.openbravo.pos.ticket.TicketLineInfo;
import com.openbravo.pos.ticket.TicketProductInfo;

// the index they've just changed
index = sales.getSelectedIndex();

if (index != -1) // when is it -1 anyway?
{
	line = ticket.getLine(index);
	ProductID = line.getProductID();

	// see if this product is on autodiscount
	autolinediscount = Integer.parseInt(line.getProperty("AutoLineDiscount", "0")); // 0 to 100
	autolinediscount_expiry = Long.parseLong(line.getProperty("AutoLineDiscount_Expiry", "0"));
	
	now = new Date();
	expiry = new Date(autolinediscount_expiry); // milliseconds!
	if (autolinediscount > 0 && expiry.after(now))
	{
		sdiscount = Formats.PERCENT.formatValue((autolinediscount/100.0));

		int reply = JOptionPane.showConfirmDialog(null, "Apply a Line Discount?\n" + line.getProductName() + " -" + sdiscount, "Discount Check", JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.YES_OPTION)
		{
			tmpLine = new TicketLineInfo(
				line.getProductID(),
				line.getProductName() + " -" + sdiscount,
				line.getProductTaxCategoryID(),
				line.getMultiply(),
				line.getPrice () * (1 - autolinediscount/100.0),
				line.getTaxInfo()
			);
			ticket.setLine(index, tmpLine);
		}
	}
}
