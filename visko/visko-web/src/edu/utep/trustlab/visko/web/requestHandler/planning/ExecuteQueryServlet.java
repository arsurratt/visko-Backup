/*
Copyright (c) 2012, University of Texas at El Paso
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, are permitted
provided that the following conditions are met:

	-Redistributions of source code must retain the above copyright notice, this list of conditions
	 and the following disclaimer.
	-Redistributions in binary form must reproduce the above copyright notice, this list of conditions
	 and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.*/


package edu.utep.trustlab.visko.web.requestHandler.planning;

import java.util.ArrayList;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.utep.trustlab.visko.web.context.ViskoWebSession;
import edu.utep.trustlab.visko.web.html.*;
import edu.utep.trustlab.visko.web.requestHandler.RequestHandlerHTML;
import edu.utep.trustlab.visko.planning.Query;
import edu.utep.trustlab.visko.planning.QueryEngine;

public class ExecuteQueryServlet extends RequestHandlerHTML {

	private Query query;
	private QueryEngine engine;
	
	public String doGet(HttpServletRequest request){
		String stringQuery = request.getParameter("query");
		String reuse = request.getParameter("reuse");
		
		if(reuse != null){
			ViskoWebSession session = (ViskoWebSession)request.getSession().getAttribute(ViskoWebSession.SESSION_ID);
			engine = session.getQueryEngine();
			query = engine.getQuery();
		}
		
		else{
			query = new Query(stringQuery);
			engine = new QueryEngine(query);
		}

		String html = "<h2>VisKo Query</h2>";
		
		if (query.isValidQuery()) {
			// if valid query add the query engine to the session
			ViskoWebSession session = new ViskoWebSession();
			session.setQueryEngine(engine);
			request.getSession().setAttribute(ViskoWebSession.SESSION_ID, session);

			html += QueryHTML.getHTML(query);
			
			html += "<h2>Visualization Pipelines</h2>";
			html += ResultsTableHTML.getHTML(engine, true);
		}
		
		else {
		
			html += "<p>Query is invalid!</p>";
			html += QueryHTML.getHTML(query);
		}

		html += "<h2>Messages</h2>";
		html += QueryMessages.getQueryMessagesHTML(query);
		return html;
	}

	public TreeMap<String, ArrayList<String>> getTreeMap( HttpServletRequest request, HttpServletResponse response) {
		String stringQuery = request.getParameter("query");
		String reuse = request.getParameter("reuse");
		
		if(reuse != null){
			ViskoWebSession session = (ViskoWebSession)request.getSession().getAttribute(ViskoWebSession.SESSION_ID);
			engine = session.getQueryEngine();
			query = engine.getQuery();
		}
		
		else{
			query = new Query(stringQuery);
			engine = new QueryEngine(query);
		}

		if (query.isValidQuery()) {
			// if valid query add the query engine to the session
			ViskoWebSession session = new ViskoWebSession();
			session.setQueryEngine(engine);
			request.getSession().setAttribute(ViskoWebSession.SESSION_ID, session);

			//HashMap<String, ArrayList<String>> resultMap = new HashMap<String, ArrayList<String>>();
			//resultMap = ResultsTableHTML.getHashMap(engine);
			//resultMap = ResultsTableHTML.getHTML(engine, true);
		
			return ResultsTableHTML.getTreeMap(engine);
		}
		else {
		
			return null;
		}
	}
	
	public TreeMap<String, ArrayList<String>> getTreeMapBuild( HttpServletRequest request, HttpServletResponse response) {
		
		String stringQuery = writeQuery(request);		
		String reuse = request.getParameter("reuse");
		
		if(reuse != null){
			ViskoWebSession session = (ViskoWebSession)request.getSession().getAttribute(ViskoWebSession.SESSION_ID);
			engine = session.getQueryEngine();
			query = engine.getQuery();
		}
		
		else{
			query = new Query(stringQuery);
			engine = new QueryEngine(query);
		}

		if (query.isValidQuery()) {
			// if valid query add the query engine to the session
			ViskoWebSession session = new ViskoWebSession();
			session.setQueryEngine(engine);
			request.getSession().setAttribute(ViskoWebSession.SESSION_ID, session);
		
			return ResultsTableHTML.getTreeMap(engine);
		}
		else {
		
			return null;
		}
	}
	
	public String writeQuery(HttpServletRequest request){	
		String defaultValue = SelectionOptionsHTML.DEFAULT;
		
		String abstractionType = (String) request.getParameter("abstractionType");
		String viewerSet = (String) request.getParameter("viewerSet");
		String InputUrlFormat = (String) request.getParameter("inputUrlFormat");
		String InputUrlType = (String) request.getParameter("inputUrlType");
		String inputUrl = (String) request.getParameter("inputUrl");	
		
		String query = "";
		
		if(inputUrl != null && inputUrl != ""){
			query = query + "VISUALIZE " + inputUrl + " \n";
		}
		else{
			query = query + "VISUALIZE url \n";
		}
	
		if(abstractionType != null && abstractionType != defaultValue){
			query = query + "AS http://openvisko.org/rdf/ontology/visko-view.owl#" + abstractionType + " \n";
		}
		else{
			query = query + "AS view";
		}
		
		if(viewerSet != null && viewerSet != defaultValue){
			query = query + "IN " + viewerSet + " \n";
		}
		else{
			query = query + "IN viewer-set \n";
		}
		
		if(InputUrlFormat != null && InputUrlFormat != defaultValue)
		{
			query = query + "WHERE\n";
			query = query + "\tFORMAT = " + InputUrlFormat + "\n";
		}
		else{
			query = query + "\tWHERE\n\tFORMAT = format\n";
			}
		
		if(InputUrlType != null && InputUrlType != defaultValue){
			query = query + "\tAND TYPE = " + InputUrlType + "\n";
		}
		else{
			query = query + "\tAND TYPE = type\n";
		}
			
		return query;
	}
}