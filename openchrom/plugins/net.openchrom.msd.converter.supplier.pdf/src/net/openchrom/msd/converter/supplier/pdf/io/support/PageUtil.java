/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.msd.converter.supplier.pdf.io.support;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.eclipse.chemclipse.logging.core.Logger;

/*
 * All public method x,y values are handled in the given unit.
 * All private method x,y values are using the default PDF values pt and and page center bottom left(0,0).
 */
public class PageUtil {

	private static final Logger logger = Logger.getLogger(PageUtil.class);
	//
	private static final String TEXT_SHORTEN_MARKER = "...";
	/*
	 * Initialized in constructor
	 */
	private PDDocument document = null;
	private PDPage page = null;
	private PDPageContentStream contentStream = null;
	private IBaseConverter baseConverter = null;
	private IUnitConverter unitConverter = null;
	private boolean landscape;

	public PageUtil(PDDocument document, PageSettings pageSettings) throws IOException {
		this.document = document;
		page = new PDPage(pageSettings.getPDRectangle());
		this.document.addPage(page);
		contentStream = new PDPageContentStream(document, page);
		//
		baseConverter = ConverterFactory.getInstance(pageSettings.getBase());
		unitConverter = ConverterFactory.getInstance(pageSettings.getUnit());
		this.landscape = pageSettings.isLandscape();
		//
		if(this.landscape) {
			page.setRotation(-90);
			float x = 0;
			float y = pageSettings.getPDRectangle().getHeight();
			contentStream.transform(Matrix.getTranslateInstance(x, y));
			contentStream.transform(Matrix.getRotateInstance(Math.toRadians(-90), 0, 0));
		}
	}

	@Override
	protected void finalize() throws Throwable {

		close();
		super.finalize();
	}

	public PDPage getPage() {

		return page;
	}

	/**
	 * Call close when page creation is finished.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {

		if(contentStream != null) {
			contentStream.close();
		}
	}

	public void printText(TextElement textElement) throws IOException {

		String text = textElement.getText();
		if(text.length() > 0) {
			switch(textElement.getTextOption()) {
				case NONE:
					printTextDefault(textElement);
					break;
				case SHORTEN:
					printTextShorten(textElement);
					break;
				case MULTI_LINE:
					printTextMultiLine(textElement);
					break;
				default:
					logger.warn("Option not supported: " + textElement.getTextOption());
					logger.warn("Option selected instead: " + TextOption.NONE);
					printTextDefault(textElement);
					break;
			}
		}
	}

	public void printImage(ImageElement imageElement) throws IOException {

		PDImageXObject image = imageElement.getImage();
		float width = convert(imageElement.getWidth());
		float height = convert(imageElement.getHeight());
		float x = calculateX(imageElement, width, width);
		float y = calculateY(imageElement, height);
		//
		printImage(image, x, y, width, height);
	}

	public void printLine(LineElement lineElement) throws IOException {

		float width = convert(lineElement.getWidth());
		float x0 = getPositionX(convert(lineElement.getX()));
		float y0 = getPositionY(convert(lineElement.getY()));
		float x1 = getPositionX(convert(lineElement.getX1()));
		float y1 = getPositionY(convert(lineElement.getY1()));
		//
		contentStream.setStrokingColor(lineElement.getColor());
		contentStream.setLineWidth(width);
		contentStream.moveTo(x0, y0);
		contentStream.lineTo(x1, y1);
		contentStream.stroke();
	}

	public void printBox(BoxElement boxElement) throws IOException {

		Color color = boxElement.getColor();
		float x = getPositionX(convert(boxElement.getX()));
		float y = getPositionY(convert(boxElement.getY() + boxElement.getHeight()));
		float width = convert(boxElement.getWidth());
		float height = convert(boxElement.getHeight());
		//
		contentStream.setNonStrokingColor(color);
		contentStream.addRect(x, y, width, height);
		contentStream.fill();
	}

	public void printTable(TableElement tableElement) throws IOException {

		if(isTableValid(tableElement)) {
			/*
			 * Settings
			 */
			float x = tableElement.getX();
			float y = tableElement.getY();
			PDFTable pdfTable = tableElement.getPdfTable();
			//
			float width = pdfTable.getWidth();
			float height = pdfTable.getColumnHeight();
			int startIndex = pdfTable.getRowStart() - 1;
			int stopIndex = pdfTable.getRowStop() - 1;
			//
			List<String> titles = pdfTable.getTitles();
			List<Float> bounds = pdfTable.getBounds();
			List<List<String>> rows = pdfTable.getRows();
			/*
			 * Header
			 */
			List<TableCell> titleCells = new ArrayList<TableCell>();
			for(int i = 0; i < titles.size(); i++) {
				titleCells.add(new TableCell(titles.get(i), bounds.get(i)));
			}
			y += printTableLine(pdfTable.getFontBold(), pdfTable.getFont(), pdfTable.getFontSize(), x, y, width, height, titleCells, Color.GRAY, true, true);
			/*
			 * Data
			 */
			if(pdfTable.getNumberRows() > 0) {
				for(int i = startIndex; i <= stopIndex; i++) {
					List<String> row = rows.get(i);
					List<TableCell> rowCells = new ArrayList<TableCell>();
					for(int j = 0; j < row.size(); j++) {
						String cell = row.get(j);
						rowCells.add(new TableCell(cell, bounds.get(j)));
					}
					Color backgroundColor = (i % 2 == 0) ? null : Color.LIGHT_GRAY;
					y += printTableLine(pdfTable.getFontBold(), pdfTable.getFont(), pdfTable.getFontSize(), x, y, width, height, rowCells, backgroundColor, false, true);
				}
			}
			/*
			 * Print last line.
			 */
			printTableLines(x, y, width, height, tableElement.getY(), titleCells);
		} else {
			logger.warn("The PDFTable is invalid.");
		}
	}

	private boolean isTableValid(TableElement tableElement) {

		PDFTable pdfTable = tableElement.getPdfTable();
		boolean isValid = pdfTable.isValid();
		//
		if(isValid) {
			float widthTable = convert(tableElement.getX() + pdfTable.getWidth());
			float widthPage = getPageWidth();
			if(widthTable > widthPage) {
				logger.warn("The table width (" + widthTable + ")is larger then the page width (" + widthPage + ").");
			}
			/*
			 * +2 = Header + Offset
			 */
			float heightTable = convert((pdfTable.getRowStop() - pdfTable.getRowStart() + 2) * pdfTable.getColumnHeight() + tableElement.getY());
			float heightPage = getPageHeight();
			if(heightTable > heightPage) {
				logger.warn("The table height (" + heightTable + ")is larger then the page height (" + heightPage + ").");
			}
		}
		//
		return isValid;
	}

	/*
	 * Page Height (pt)
	 */
	private float getPageHeight() {

		PDRectangle rectangle = page.getMediaBox();
		return (landscape) ? rectangle.getWidth() : rectangle.getHeight();
	}

	/*
	 * Page Width (pt)
	 */
	private float getPageWidth() {

		PDRectangle rectangle = page.getMediaBox();
		return (landscape) ? rectangle.getHeight() : rectangle.getWidth();
	}

	/*
	 * Text height (pt)
	 */
	private float calculateTextHeight(PDFont font, float fontSize) {

		PDRectangle rectangle = font.getFontDescriptor().getFontBoundingBox();
		return (rectangle.getHeight() / 1000.0f * fontSize * 0.68f);
	}

	/*
	 * Text width (pt)
	 */
	private float calculateTextWidth(PDFont font, float fontSize, String text) throws IOException {

		return (font.getStringWidth(text) / 1000.0f * fontSize);
	}

	/*
	 * X (pt) - 0,0 left bottom
	 */
	private float getPositionX(float x) {

		return baseConverter.getPositionX(getPageWidth(), x);
	}

	/*
	 * Y (pt) - 0,0 left bottom
	 */
	private float getPositionY(float y) {

		return baseConverter.getPositionY(getPageHeight(), y);
	}

	/*
	 * Convert value to pt
	 */
	private float convert(float value) {

		return unitConverter.convert(value);
	}

	/*
	 * x, y (pt)
	 * @param font
	 * @param fontSize
	 * @param x
	 * @param y
	 * @param text
	 * @throws IOException
	 */
	private void printText(PDFont font, float fontSize, float x, float y, String text) throws IOException {

		contentStream.setFont(font, fontSize);
		contentStream.beginText();
		contentStream.newLineAtOffset(x, y);
		contentStream.showText(text);
		contentStream.endText();
	}

	/*
	 * x, y, width, height (pt)
	 * @param image
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @throws IOException
	 */
	private void printImage(PDImageXObject image, float x, float y, float width, float height) throws IOException {

		contentStream.drawImage(image, x, y, width, height);
	}

	private float printTableLine(PDFont fontBold, PDFont font, float fontSize, float x, float y, float width, float height, List<TableCell> cells, Color color, boolean bold, boolean drawBottomLine) throws IOException {

		/*
		 * Background
		 */
		if(color != null) {
			printBox(new BoxElement(x, y, width, height).setColor(color));
		}
		/*
		 * Print the text
		 */
		contentStream.setStrokingColor(Color.WHITE); // Background/Border
		contentStream.setNonStrokingColor(Color.BLACK); // Text
		float xLeft = x; // + 1mm? instead of whitespace " " + cell.getText()
		float yText = y + 1; // TODO + 1mm
		for(TableCell cell : cells) {
			printText(new TextElement(xLeft, yText, cell.getWidth()).setFont((bold) ? fontBold : font).setFontSize(fontSize).setText(" " + cell.getText()));
			xLeft += cell.getWidth();
		}
		/*
		 * Bottom Line
		 */
		if(drawBottomLine) {
			contentStream.setStrokingColor(Color.BLACK); // Background/Border
			contentStream.setNonStrokingColor(Color.BLACK); // Text
			float yBottom = y + height;
			printLine(new LineElement(x, yBottom, x + width, yBottom).setWidth(0.2f));
		}
		//
		return height;
	}

	private void printTableLines(float x, float y, float width, float height, float yStartPosition, List<TableCell> cells) throws IOException {

		contentStream.setStrokingColor(Color.BLACK); // Background/Border
		contentStream.setNonStrokingColor(Color.BLACK); // Text
		/*
		 * Top Line
		 */
		printLine(new LineElement(x, yStartPosition, x + width, yStartPosition).setWidth(0.2f));
		/*
		 * Print vertical lines.
		 */
		//
		float yStart = yStartPosition;
		float yStartExtraSpace = yStartPosition; // + 5.0f; // 5.385835pt -> 1.9 mm
		float yStop = y;
		float xLeft = x;
		for(TableCell cell : cells) {
			if(cell.isPrintLeftLine()) {
				printLine(new LineElement(xLeft, yStart, xLeft, yStop).setWidth(0.2f));
			} else {
				printLine(new LineElement(xLeft, yStartExtraSpace, xLeft, yStop).setWidth(0.2f));
			}
			xLeft += cell.getWidth();
		}
		//
		printLine(new LineElement(x + width, yStart, x + width, yStop).setWidth(0.2f));
	}

	@SuppressWarnings("rawtypes")
	private float calculateX(IReferenceElement referenceElement, float elementWidth, float maxWidth) throws IOException {

		float x;
		ReferenceX referenceX = referenceElement.getReferenceX();
		switch(referenceX) {
			case LEFT:
				x = getPositionX(convert(referenceElement.getX()));
				break;
			case RIGHT:
				x = getPositionX(convert(referenceElement.getX())) + maxWidth - elementWidth;
				break;
			default:
				logger.warn("Option not supported: " + referenceX);
				logger.warn("Option selected instead: " + ReferenceX.LEFT);
				x = getPositionX(convert(referenceElement.getX()));
				break;
		}
		return x;
	}

	@SuppressWarnings("rawtypes")
	private float calculateY(IReferenceElement referenceElement, float elementHeight) {

		float y;
		ReferenceY referenceY = referenceElement.getReferenceY();
		switch(referenceY) {
			case TOP:
				y = getPositionY(convert(referenceElement.getY())) - elementHeight;
				break;
			case CENTER:
				y = getPositionY(convert(referenceElement.getY())) - elementHeight / 2.0f;
				break;
			case BOTTOM:
				y = getPositionY(convert(referenceElement.getY()));
				break;
			default:
				logger.warn("Option not supported: " + referenceY);
				logger.warn("Option selected instead: " + ReferenceY.BOTTOM);
				y = getPositionY(convert(referenceElement.getY()));
				break;
		}
		return y;
	}

	private void printTextDefault(TextElement textElement) throws IOException {

		PDFont font = textElement.getFont();
		float fontSize = textElement.getFontSize();
		String text = textElement.getText();
		float maxWidth = convert(textElement.getMaxWidth());
		float textWidth = calculateTextWidth(font, fontSize, text);
		float textHeight = calculateTextHeight(font, fontSize);
		float x = calculateX(textElement, textWidth, maxWidth);
		float y = calculateY(textElement, textHeight);
		//
		printText(font, fontSize, x, y, text);
	}

	private void printTextShorten(TextElement textElement) throws IOException {

		PDFont font = textElement.getFont();
		float fontSize = textElement.getFontSize();
		String text = textElement.getText();
		float maxWidth = convert(textElement.getMaxWidth());
		float textWidth = calculateTextWidth(font, fontSize, text);
		float textHeight = calculateTextHeight(font, fontSize);
		float x = convert(textElement.getX());
		float y = calculateY(textElement, textHeight);
		//
		String shortenedText = text; // default
		ReferenceX referenceX = textElement.getReferenceX();
		switch(referenceX) {
			case LEFT:
				shortenedText = shortenStringLeft(text, textWidth, maxWidth);
				break;
			case RIGHT:
				shortenedText = shortenStringRight(text, textWidth, maxWidth);
				break;
			default:
				logger.warn("Option not supported: " + referenceX);
				logger.warn("Option selected instead: " + ReferenceX.LEFT);
				shortenedText = shortenStringLeft(text, textWidth, maxWidth);
				break;
		}
		//
		printText(font, fontSize, x, y, shortenedText);
	}

	private void printTextMultiLine(TextElement textElement) throws IOException {

		PDFont font = textElement.getFont();
		float fontSize = textElement.getFontSize();
		String text = textElement.getText();
		float maxWidth = convert(textElement.getMaxWidth());
		float textWidth = calculateTextWidth(font, fontSize, text);
		float minHeight = convert(textElement.getMinHeight());
		float textHeight = calculateTextHeight(font, fontSize);
		float x = convert(textElement.getX());
		float y = calculateY(textElement, textHeight);
		//
		List<String> textElements = cutStringMultiLine(text, textWidth, maxWidth);
		float offset = 0.0f;
		for(String part : textElements) {
			printText(font, fontSize, x, y - offset, part);
			offset += (textHeight > minHeight) ? textHeight : minHeight;
		}
	}

	/*
	 * textWidth, availableWidth (pt)
	 * Lorem ipsum dolor sit amet, consetetur sadipscing...
	 */
	private String shortenStringLeft(String text, float textWidth, float maxWidth) {

		int endIndex = (int)(text.length() / textWidth * maxWidth) - 3; // -3 (TEXT_CUT)
		if(textWidth > maxWidth && endIndex > 0) {
			return text.substring(0, endIndex) + TEXT_SHORTEN_MARKER;
		} else {
			return text;
		}
	}

	/*
	 * textWidth, availableWidth (pt)
	 * ...kasd gubergren, no sea takimata sanctus est.
	 */
	private String shortenStringRight(String text, float textWidth, float maxWidth) {

		int length = text.length();
		int startIndex = length - (int)((length / textWidth * maxWidth));
		if(textWidth > maxWidth && startIndex > 0) {
			return TEXT_SHORTEN_MARKER + text.substring(startIndex, length);
		} else {
			return text;
		}
	}

	private List<String> cutStringMultiLine(String text, float textWidth, float maxWidth) {

		List<String> textElements = new ArrayList<>();
		//
		int textLength = text.length();
		int partLength = (int)(textLength / textWidth * maxWidth);
		int parts = textLength / partLength + 1;
		for(int i = 0; i < parts; i++) {
			int startIndex = i * partLength;
			int stopIndex = startIndex + partLength;
			textElements.add(text.substring(startIndex, (stopIndex > textLength) ? textLength : stopIndex));
		}
		//
		return textElements;
	}
}
