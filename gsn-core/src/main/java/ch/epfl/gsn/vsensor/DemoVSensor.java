/**
* Global Sensor Networks (GSN) Source Code
* Copyright (c) 2006-2016, Ecole Polytechnique Federale de Lausanne (EPFL)
* 
* This file is part of GSN.
* 
* GSN is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* GSN is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with GSN.  If not, see <http://www.gnu.org/licenses/>.
* 
* File: src/ch/epfl/gsn/vsensor/DemoVSensor.java
*
* @author Ali Salehi
* @author Mehdi Riahi
* @author Julien Eberle
*
*/

package ch.epfl.gsn.vsensor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.LoggerFactory;

import ch.epfl.gsn.beans.DataField;
import ch.epfl.gsn.beans.DataTypes;
import ch.epfl.gsn.beans.StreamElement;
import ch.epfl.gsn.vsensor.AbstractVirtualSensor;
import ch.epfl.gsn.vsensor.DemoVSensor;

import org.slf4j.Logger;


public class DemoVSensor extends AbstractVirtualSensor {

	private static final transient Logger logger                  = LoggerFactory.getLogger( DemoVSensor.class );

	private ArrayList < String >          fields                  = new ArrayList < String >( );

	private ByteArrayOutputStream         outputStream            = new ByteArrayOutputStream( 24 * 1024 );

	private ByteArrayInputStream          input;

	private static final String           IMAGE_OUTPUT_FIELD      = "image";

	private static final int              IMAGE_OUTPUT_FIELD_TYPE = DataTypes.BINARY;

	private static final String [ ]       OUTPUT_FIELDS           = new String [ ] { IMAGE_OUTPUT_FIELD };

	private static final Byte [ ]      OUTPUT_TYPES            = new Byte [ ] { IMAGE_OUTPUT_FIELD_TYPE };

	private static BufferedImage          cachedBufferedImage     = null;

	private static int                    counter                 = 0;

	public void dataAvailable ( String inputStreamName , StreamElement data ) {
		if ( inputStreamName.equalsIgnoreCase( "SSTREAM" ) ) {
			String action = ( String ) data.getData( "STATUS" );
			/**
			 * 
			 */
			String moteId = ( String ) data.getData( "ID" );
			if ( moteId.toLowerCase( ).indexOf( "mica" ) < 0 ) return;
			if ( action.toLowerCase( ).indexOf( "add" ) >= 0 ) counter++;
			if ( action.toLowerCase( ).indexOf( "remove" ) >= 0 ) counter--;
		}
		if ( inputStreamName.equalsIgnoreCase( "CSTREAM" ) ) {

			BufferedImage bufferedImage = null;
			outputStream.reset( );
			byte [ ] rawData = ( byte [ ] ) data.getData( "IMAGE" );
			input = new ByteArrayInputStream( rawData );
			try {
				bufferedImage = ImageIO.read( input );
			} catch ( IOException e ) {
				logger.error(e.getMessage(), e);
			}
			Graphics2D graphics = ( Graphics2D ) bufferedImage.getGraphics( );
			int size = 30;
			int locX = 0;
			int locY = 0;
			if ( counter < 0 ) counter = 0;
			switch ( counter ) {
			case 0 :
				graphics.setColor( Color.RED );
				break;
			case 1 :
				graphics.setColor( Color.ORANGE );
				break;

			case 2 :
				graphics.setColor( Color.YELLOW );
				break;

			case 3 :
				graphics.setColor( Color.GREEN );
				break;
			default :
				logger.warn( new StringBuilder( ).append( "Shouldn't happen.>" ).append( counter ).append( "<" ).toString( ) );
			}
			graphics.fillOval( locX , locY , size , size );
			try {
				ImageIO.write(bufferedImage,"jpeg", outputStream);
				outputStream.close();

			}catch (Exception e) {
				logger.error(e.getMessage(),e);
			}

			StreamElement outputSE = new StreamElement( OUTPUT_FIELDS , OUTPUT_TYPES , new Serializable [ ] { outputStream.toByteArray( ) } , data.getTimeStamp( ) );
			dataProduced( outputSE );
		}
		logger.info( new StringBuilder( ).append( "Data received under the name: " ).append( inputStreamName ).toString( ) );
	}

	public boolean initialize ( ) {
		for ( DataField field : getVirtualSensorConfiguration( ).getOutputStructure( ) )
			fields.add( field.getName( ) );
		return true;
	}

	public void dispose ( ) {

	}
}
