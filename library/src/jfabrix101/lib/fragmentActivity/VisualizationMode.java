/*
 * jfabrix101 - Library to simplify the developer life
 * 
 * Copyright (C) 2013 jfabrix101 (www.fabrizio-russo.it)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version
 * 2.1, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License 2.1 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License version 2.1 along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
*/
package jfabrix101.lib.fragmentActivity;

public enum VisualizationMode {

	LANDSCAPE("LANDSCAPE", 0),
	//PORTRAIT_AS_LANDSCAPE("PORTRATI_AS_LANDSCAPE", 0),
	PORTRAIT_ONLY_LEFT("PORTRAIT_ONLY_LEFT", 1),
	PORTRAIT_ONLY_RIGHT("PORTRAIT_ONLY_RIGHT", 2);
	
	private final String visualizationName;
    private final int visualizationCode;
    
    private VisualizationMode(String name, int code ) {
		this.visualizationName = name;
		this.visualizationCode = code;
	}
    
    @Override
    public String toString() {
    	return visualizationName;
    }
    
    public static VisualizationMode getByCode(int code) {
    	for (VisualizationMode mode : VisualizationMode.values()) {
    		if (mode.visualizationCode == code) return mode;
    	}
    	throw new RuntimeException("Invalid code for VisualizationMode");
    }
}
