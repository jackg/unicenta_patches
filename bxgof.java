/*
	Cae's BxGOF

	uses product properties:
		BOGOF
			integer 1+
			1 = bogof, 2=b2gof, etc
*/

// BUGS removeline not processed properly, does this need to go in there as well?


// add this to event.change:

import com.openbravo.pos.ticket.TicketLineInfo;

// the index they've just changed
index = sales.getSelectedIndex();

if (index != -1) // when is it -1 anyway?
{
	line = ticket.getLine(index);
	ProductID = line.getProductID();

	// free = floor(items/bogof+1)
	bogof = Integer.parseInt(line.getProperty("BOGOF", "0"));

	if (bogof > 0)
	{
		// count how many products they have paid for
		item_count = 0;
		free_index = -1;

		for (i = 0 ; i < ticket.getLinesCount() ; i++)
		{
			tmpLine = ticket.getLine(i);
			tmpProductID = tmpLine.getProductID();

			// lines we've added as discount have property BOGOF_FREE
			free = Integer.parseInt(tmpLine.getProperty("BOGOF_FREE", "0"));
			freeProductID = tmpLine.getProperty("ProductID", "NOTPRESENT");
			if (free == 1 && freeProductID != null && ProductID.equals(freeProductID))
			{
				// remember this for later
				free_index = i;
			}

			if (tmpProductID != null && ProductID.equals(tmpProductID))
			{
				// they've paid for these items
				item_count += tmpLine.getMultiply();
			}
		}

		// now we know how many products they've paid for, we'll add another
		// line with BOGOF_FREE, containing the right number of items

		free_count = Math.floor(item_count/(bogof+1));

		// check if there's already a BOGOF_FREE line for this product
		if (free_index > -1)
		{
			// got it, just ensure the multiple is right
			if (free_count > 0)
			{
				tmpLine = ticket.getLine(free_index);
				tmpLine.setMultiply(free_count);
			}
			else
			{
				// remove the free line
				ticket.removeLine(free_index);
			}
		}
		else
		{
			if (free_count > 0)
			{
				// make a new line
				newLine = new TicketLineInfo(
					line.getProductName() + " (FREE)",
					line.getProductTaxCategoryID(),
					free_count, // MULTIPLY
					-line.getPrice(), // PRICE
					line.getTaxInfo()
				);
				newLine.setProperty("BOGOF_FREE", "1");
				newLine.setProperty("ProductID", ProductID);
				ticket.insertLine(ticket.getLinesCount(), newLine);
			}
		}
	}

}
