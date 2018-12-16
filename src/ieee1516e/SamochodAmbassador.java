/*
 *   Copyright 2012 The Portico Project
 *
 *   This file is part of portico.
 *
 *   portico is free software; you can redistribute it and/or modify
 *   it under the terms of the Common Developer and Distribution License (CDDL) 
 *   as published by Sun Microsystems. For more information see the LICENSE file.
 *   
 *   Use of this software is strictly AT YOUR OWN RISK!!!
 *   If something bad happens you do not have permission to come crying to me.
 *   (that goes for your lawyer as well)
 *
 */
package ieee1516e;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.FederateHandleSet;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.NullFederateAmbassador;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.OrderType;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.SynchronizationPointFailureReason;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAfloat64BE;
import hla.rti1516e.encoding.HLAinteger16BE;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.encoding.HLAinteger64BE;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.time.HLAfloat64Time;

/**
 * This class handles all incoming callbacks from the RTI regarding a particular
 * {@link ExampleFederate}. It will log information about any callbacks it
 * receives, thus demonstrating how to deal with the provided callback information.
 */
public class SamochodAmbassador extends NullFederateAmbassador
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Samochod federate;
	private int doczego;
	private short stanowisko;
	private double czas_zakonczenia;
	// these variables are accessible in the package
	protected double federateTime        = 0.0;
	protected double federateLookahead   = 1.0;
	
	protected boolean isRegulating       = false;
	protected boolean isConstrained      = false;
	protected boolean isAdvancing        = false;
	
	protected boolean isAnnounced        = false;
	protected boolean isReadyToRun       = false;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	public SamochodAmbassador( Samochod federate )
	{
		this.federate = federate;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	private void log( String message )
	{
		System.out.println( "FederateAmbassador: " + message );
	}
	
	private int decodeDlugosc_kolejki( byte[] bytes )
	{
		HLAinteger32BE value = federate.encoderFactory.createHLAinteger32BE();
		// decode
		try
		{
			value.decode( bytes );
			return value.getValue();
		}
		catch( DecoderException de )
		{
			de.printStackTrace();
			return 0;
		}
		
	}

	private short decodeTyp_kolejki( byte[] bytes )
	{
		HLAinteger16BE value = federate.encoderFactory.createHLAinteger16BE();
		// decode
		try
		{
			value.decode( bytes );
			return value.getValue();
		}
		catch( DecoderException de )
		{
			de.printStackTrace();
			return 0;
		}
	}
	
	private short decodeStanowisko(byte[] bytes) {
		HLAinteger16BE value = federate.encoderFactory.createHLAinteger16BE();
		// decode
		try
		{
			value.decode( bytes );
			return value.getValue();
		}
		catch( DecoderException de )
		{
			de.printStackTrace();
			return 0;
		}
	}
	
	private int decodenrstanowiska(byte[] bytes) {
		HLAinteger32BE value = federate.encoderFactory.createHLAinteger32BE();
		// decode
		try
		{
			value.decode( bytes );
			return value.getValue();
		}
		catch( DecoderException de )
		{
			de.printStackTrace();
			return 0;
		}
	}

	private double decodeCzas_rozpoczecia(byte[] bytes) {
		HLAfloat64BE value = federate.encoderFactory.createHLAfloat64BE();
		// decode
		try
		{
			value.decode( bytes );
			return value.getValue();
		}
		catch( DecoderException de )
		{
			de.printStackTrace();
			return 0;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	////////////////////////// RTI Callback Methods //////////////////////////
	//////////////////////////////////////////////////////////////////////////
	@Override
	public void synchronizationPointRegistrationFailed( String label,
	                                                    SynchronizationPointFailureReason reason )
	{
		log( "Failed to register sync point: " + label + ", reason="+reason );
	}

	@Override
	public void synchronizationPointRegistrationSucceeded( String label )
	{
		log( "Successfully registered sync point: " + label );
	}

	@Override
	public void announceSynchronizationPoint( String label, byte[] tag )
	{
		log( "Synchronization point announced: " + label );
		if( label.equals(Samochod.READY_TO_RUN) )
			this.isAnnounced = true;
	}

	@Override
	public void federationSynchronized( String label, FederateHandleSet failed )
	{
		log( "Federation Synchronized: " + label );
		if( label.equals(Samochod.READY_TO_RUN) )
			this.isReadyToRun = true;
	}

	/**
	 * The RTI has informed us that time regulation is now enabled.
	 */
	@Override
	public void timeRegulationEnabled( LogicalTime time )
	{
		this.federateTime = ((HLAfloat64Time)time).getValue();
		this.isRegulating = true;
	}

	@Override
	public void timeConstrainedEnabled( LogicalTime time )
	{
		this.federateTime = ((HLAfloat64Time)time).getValue();
		this.isConstrained = true;
	}

	@Override
	public void timeAdvanceGrant( LogicalTime time )
	{
		this.federateTime = ((HLAfloat64Time)time).getValue();
		this.isAdvancing = false;
	}

	@Override
	public void discoverObjectInstance( ObjectInstanceHandle theObject,
	                                    ObjectClassHandle theObjectClass,
	                                    String objectName )
	    throws FederateInternalError
	{
		log( "Discoverd Object: handle=" + theObject + ", classHandle=" +
		     theObjectClass + ", name=" + objectName );
	}

	@Override
	public void reflectAttributeValues( ObjectInstanceHandle theObject,
	                                    AttributeHandleValueMap theAttributes,
	                                    byte[] tag,
	                                    OrderType sentOrder,
	                                    TransportationTypeHandle transport,
	                                    SupplementalReflectInfo reflectInfo )
	    throws FederateInternalError
	{
			// just pass it on to the other method for printing purposes
			// passing null as the time will let the other method know it
			// it from us, not from the RTI
			reflectAttributeValues( theObject,
			                        theAttributes,
			                        tag,
			                        sentOrder,
			                        transport,
			                        null,
			                        sentOrder,
			                        reflectInfo );
	}

	@Override
	public void reflectAttributeValues( ObjectInstanceHandle theObject,
	                                    AttributeHandleValueMap theAttributes,
	                                    byte[] tag,
	                                    OrderType sentOrdering,
	                                    TransportationTypeHandle theTransport,
	                                    LogicalTime time,
	                                    OrderType receivedOrdering,
	                                    SupplementalReflectInfo reflectInfo )
	    throws FederateInternalError
	{
		StringBuilder builder = new StringBuilder( "Reflection for object:" );
		
		// print the handle
		builder.append( " handle=" + theObject );
		// print the tag
		builder.append( ", tag=" + new String(tag) );
		// print the time (if we have it) we'll get null if we are just receiving
		// a forwarded call from the other reflect callback above
		if( time != null )
		{
			builder.append( ", time=" + ((HLAfloat64Time)time).getValue() );
		}
		
		// print the attribute information
		builder.append( ", attributeCount=" + theAttributes.size() );
		builder.append( "\n" );
		for( AttributeHandle attributeHandle : theAttributes.keySet() )
		{
			// print the attibute handle
			builder.append( "\tattributeHandle=" );

			// if we're dealing with Flavor, decode into the appropriate enum value
			
			if( attributeHandle.equals(federate.typ_kolejkiHandle) )
			{
				builder.append( attributeHandle );
				builder.append( " Typ kolejki    " );
				builder.append( ", attributeValue=" );
				builder.append( decodeTyp_kolejki(theAttributes.get(attributeHandle)) );
				if (decodeTyp_kolejki(theAttributes.get(attributeHandle)) == 1) {
					doczego = 0;
				}
				else if (decodeTyp_kolejki(theAttributes.get(attributeHandle)) == 2) {
					doczego = 1;
				}
				else if (decodeTyp_kolejki(theAttributes.get(attributeHandle)) == 3) {
					doczego = 2;
				}
			}
			else if( attributeHandle.equals(federate.dlugosc_kolejkiHandle) )
			{
				builder.append( attributeHandle );
				builder.append( " Długość Kolejki    " );
				builder.append( ", attributeValue=" );
				builder.append( decodeDlugosc_kolejki(theAttributes.get(attributeHandle)) );
				federate.kolejkado[doczego] = decodeDlugosc_kolejki(theAttributes.get(attributeHandle));
				if (doczego == 0) federate.dostalemdys = true;
				else if (doczego == 1) federate.dostalemmyj = true;
				else if (doczego == 2) federate.dostalemkas = true;
			}
			else
			{
				builder.append( attributeHandle );
				builder.append( " (Unknown)   " );
			}
			
			builder.append( "\n" );
		}
		
		log( builder.toString() );
	}

	@Override
	public void receiveInteraction( InteractionClassHandle interactionClass,
	                                ParameterHandleValueMap theParameters,
	                                byte[] tag,
	                                OrderType sentOrdering,
	                                TransportationTypeHandle theTransport,
	                                SupplementalReceiveInfo receiveInfo )
	    throws FederateInternalError
	{
		// just pass it on to the other method for printing purposes
		// passing null as the time will let the other method know it
		// it from us, not from the RTI
		this.receiveInteraction( interactionClass,
		                         theParameters,
		                         tag,
		                         sentOrdering,
		                         theTransport,
		                         null,
		                         sentOrdering,
		                         receiveInfo );
	}

	@Override
	public void receiveInteraction( InteractionClassHandle interactionClass,
	                                ParameterHandleValueMap theParameters,
	                                byte[] tag,
	                                OrderType sentOrdering,
	                                TransportationTypeHandle theTransport,
	                                LogicalTime time,
	                                OrderType receivedOrdering,
	                                SupplementalReceiveInfo receiveInfo )
	    throws FederateInternalError
	{
		StringBuilder builder = new StringBuilder( "Interaction Received:" );
		
		// print the handle
		builder.append( " handle=" + interactionClass );
		if( interactionClass.equals(federate.rozpoczecie_obslugiHandle) )
		{
			builder.append( " Rozpoczęcie obługi" );
		}
		else if ( interactionClass.equals(federate.zakonczenie_obslugiHandle))
		{
			builder.append(" Zakończenie obsługi");
		}
		
		// print the tag
		builder.append( ", tag=" + new String(tag) );
		// print the time (if we have it) we'll get null if we are just receiving
		// a forwarded call from the other reflect callback above
		if( time != null )
		{
			builder.append( ", time=" + ((HLAfloat64Time)time).getValue() );
		}
		
		// print the parameter information
		builder.append( ", parameterCount=" + theParameters.size() );
		builder.append( "\n" );
		for( ParameterHandle parameter : theParameters.keySet() )
		{
			// print the parameter handle
			builder.append( "\tparamHandle=" );
			builder.append( parameter );
			// print the parameter value
			if( parameter.equals(federate.czas_rozpoczeciaHandle) )
			{
				builder.append( parameter );
				builder.append( " Czas rozpoczęcia    " );
				builder.append( ", parameterValue=" );
				builder.append( decodeCzas_rozpoczecia(theParameters.get(parameter)) );
			}
			else if( parameter.equals(federate.stanowiskorHandle) )
			{
				builder.append( parameter );
				builder.append( " Stanowisko    " );
				builder.append( ", parameterValue=" );
				builder.append( decodeStanowisko(theParameters.get(parameter)) );
				stanowisko = decodeStanowisko(theParameters.get(parameter));
				if (stanowisko == 1) {

					//federate.zajetamyjnia = true;
					//federate.zajetakasa = true;
				}
				else if (stanowisko == 2) {

					federate.getdostalemmyj = true;
					//federate.zajetakasa = true;
				}
				else {

					//federate.zajetamyjnia = true;
					federate.getdostalemkas = true;
				}
			}
			else if( parameter.equals(federate.nrstanowiskarHandle) ) {
				builder.append( parameter );
				builder.append( " Nr stanowiska    " );
				builder.append( ", parameterValue=" );
				builder.append( decodenrstanowiska(theParameters.get(parameter)) );
				if (stanowisko == 1) federate.dystrybutory[decodenrstanowiska(theParameters.get(parameter))].dostalem();
			}
			
			else if( parameter.equals(federate.nr_samochodurHandle) ) {
				builder.append( parameter );
				builder.append( " Nr samochodu    " );
				builder.append( ", parameterValue=" );
				builder.append( decodenrstanowiska(theParameters.get(parameter)) );
				federate.samochody[decodenrstanowiska(theParameters.get(parameter))].wtrakcie = true;
			}
			
			else if( parameter.equals(federate.czas_zakonczeniaHandle) )
			{
				builder.append( parameter );
				builder.append( " Czas zakończenia    " );
				builder.append( ", parameterValue=" );
				builder.append( decodeCzas_rozpoczecia(theParameters.get(parameter)) );
				czas_zakonczenia = decodeCzas_rozpoczecia(theParameters.get(parameter));
			}
			else if( parameter.equals(federate.stanowiskozHandle) )
			{
				builder.append( parameter );
				builder.append( " Stanowisko    " );
				builder.append( ", parameterValue=" );
				builder.append( decodeStanowisko(theParameters.get(parameter)) );
				stanowisko = decodeStanowisko(theParameters.get(parameter));
				if (stanowisko == 1) {
						
					//federate.zajetamyjnia = false;
					//federate.zajetakasa = true;
				}
				else if (stanowisko == 2) {

					federate.zajetamyjnia = false;
					federate.getdostalemmyj = false;
					//federate.zajetakasa = false;
				}
				else {

					//federate.zajetamyjnia = true;
					federate.zajetakasa = false;
					federate.getdostalemkas = false;
				}
			}
			else if( parameter.equals(federate.nrstanowiskazHandle) ) {
				builder.append( parameter );
				builder.append( " Nr stanowiska    " );
				builder.append( ", parameterValue=" );
				builder.append( decodenrstanowiska(theParameters.get(parameter)) );
				if (stanowisko == 1) federate.dystrybutory[decodenrstanowiska(theParameters.get(parameter))].koniectankowania();
			}
			else if( parameter.equals(federate.nr_samochoduzHandle) ) {
				builder.append( parameter );
				builder.append( " Nr samochodu    " );
				builder.append( ", parameterValue=" );
				builder.append( decodenrstanowiska(theParameters.get(parameter)) );
				federate.samochody[decodenrstanowiska(theParameters.get(parameter))].wtrakcie = false;
				if (stanowisko == 1 && czas_zakonczenia % 2 == 1) federate.samochody[decodenrstanowiska(theParameters.get(parameter))].nrstanowiska = 4;
				else if (stanowisko == 1 && czas_zakonczenia % 2 == 0) federate.samochody[decodenrstanowiska(theParameters.get(parameter))].nrstanowiska = 1;
				else if (stanowisko == 2) federate.samochody[decodenrstanowiska(theParameters.get(parameter))].nrstanowiska = 2;
				else if (stanowisko == 3) federate.samochody[decodenrstanowiska(theParameters.get(parameter))].nrstanowiska = 3;
			}
			else
			{
				builder.append( parameter );
				builder.append( " (Unknown)   " );
			}
		}

		log( builder.toString() );
	}



	@Override
	public void removeObjectInstance( ObjectInstanceHandle theObject,
	                                  byte[] tag,
	                                  OrderType sentOrdering,
	                                  SupplementalRemoveInfo removeInfo )
	    throws FederateInternalError
	{
		log( "Object Removed: handle=" + theObject );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
