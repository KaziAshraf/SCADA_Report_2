package application;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class Footer extends PdfPageEventHelper {

	public void onEndPage(PdfWriter writer, Document document) {
		PdfContentByte cb = writer.getDirectContent();
		ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
				new Phrase(String.format("Page %d ", writer.getPageNumber()),
						FontFactory.getFont(FontFactory.HELVETICA)),
				(document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0);
	}

}
