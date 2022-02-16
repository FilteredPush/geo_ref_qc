/**
 * 
 */
package org.filteredpush.qc.georeference.util;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileException;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.shapefile.shp.ShapefileReader.Record;
import org.opengis.geometry.Boundary;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.primitive.Primitive;

/**
 * @author mole
 *
 */
public class GISDataLoader {

    public Set<Path2D> ReadLandData() throws IOException, ShapefileException {

        ShpFiles is = new ShpFiles(GISDataLoader.class.getResource("/org.filteredpush.kuration.services/ne_10m_land.shp"));
        //FileInputStream is = null;
        //is = new FileInputStream("/etc/filteredpush/descriptors/ne_10m_land.shp");

        ShapefileReader reader = null;
        reader = new ShapefileReader(is, false, false, null);

        Set<Path2D> polygonSet = new HashSet<Path2D>();

        Geometry shape;
        while (reader.hasNext() ) {
        	shape = (Geometry) reader.nextRecord().shape();
        	
        	// TODO: Rework to extract points using geotools.  
        	
//            for (int i = 0; i < aPolygon.getNumberOfParts(); i++) {
//                PointData[] points = aPolygon.getPointsOfPart(i);
//                //System.out.println("- part " + i + " has " + points.length + " points");
//
//                Path2D polygon = new Path2D.Double();
//                for (int j = 0; j < points.length; j++) {
//                    if (j==0) polygon.moveTo(points[j].getX(), points[j].getY());
//                    else polygon.lineTo(points[j].getX(), points[j].getY());
//                    //System.out.println("- point " + i + " has " + points[j].getX() + " and " + points[j].getY());
//                }
//                polygonSet.add(polygon);
//            }
        }
        reader.close();
        is.dispose();
        return polygonSet;
    }
	
	
}
