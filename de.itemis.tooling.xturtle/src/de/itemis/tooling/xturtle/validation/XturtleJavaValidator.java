/*********************************************************************************
 * Copyright (c) 2013-2015 AKSW Xturtle Project, itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *********************************************************************************/
package de.itemis.tooling.xturtle.validation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.nodemodel.BidiTreeIterator;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;

import com.google.common.base.Optional;
import com.google.inject.Inject;

import de.itemis.tooling.xturtle.resource.TurtleResourceService;
import de.itemis.tooling.xturtle.services.Prefixes;
import de.itemis.tooling.xturtle.xturtle.BlankObjects;
import de.itemis.tooling.xturtle.xturtle.Directives;
import de.itemis.tooling.xturtle.xturtle.Predicate;
import de.itemis.tooling.xturtle.xturtle.PredicateObjectList;
import de.itemis.tooling.xturtle.xturtle.PrefixId;
import de.itemis.tooling.xturtle.xturtle.QNameDef;
import de.itemis.tooling.xturtle.xturtle.QNameRef;
import de.itemis.tooling.xturtle.xturtle.Resource;
import de.itemis.tooling.xturtle.xturtle.StringLiteral;
import de.itemis.tooling.xturtle.xturtle.Triples;
import de.itemis.tooling.xturtle.xturtle.UriDef;
import de.itemis.tooling.xturtle.xturtle.XturtlePackage;
 

public class XturtleJavaValidator extends AbstractXturtleJavaValidator {
	@Inject 
	private Prefixes prefixes;
	@Inject 
	private TurtleResourceService service;
	@Inject
	private TurtleValidationSeverityLevels levels;
	@Inject
	private TurtleLinkingErrorExceptions linkingErrorExceptions;

	public static final String UNKNOWN_PREFIX="unknownPrefix";

	@Check(CheckType.NORMAL)
	public void checkAxiomSyntax(Triples triples) {
		if(!(triples.getSubject() instanceof BlankObjects) && triples.getPredObjs().isEmpty()){
			//raise the error only if the subject has no syntax errors
			ICompositeNode node = NodeModelUtils.getNode(triples.getSubject());
			BidiTreeIterator<INode> iterator = node.getAsTreeIterable().iterator();
			while(iterator.hasNext()){
				if(iterator.next().getSyntaxErrorMessage()!=null){
					return;
				}
			}
			error("predicate object list is optional only for blank",XturtlePackage.Literals.TRIPLES__SUBJECT,"axiom");
		}
	}

	private boolean containsWhitespace(EObject o){
		Iterable<ILeafNode> leaves = NodeModelUtils.findActualNodeFor(o).getLeafNodes();
		boolean hiddenOK=true;
		for (ILeafNode leaf : leaves) {
			if(leaf.isHidden()){
				if(!hiddenOK){
					return true;
				}
			}else{
				hiddenOK=false;
			}
		}
		return false;
	}

	@Check(CheckType.EXPENSIVE)
	public void checkNoWhitespaceInQName(QNameRef qnameRef) {
		if(containsWhitespace(qnameRef)){
			error("qname must not contain whitespaces or comments",XturtlePackage.Literals.RESOURCE_REF__REF,"qnameWS");
		}
	}

	@Check(CheckType.EXPENSIVE)
	public void checkNoWhitespaceInQName(QNameDef qnameRef) {
		if(containsWhitespace(qnameRef)){
			error("qname must not contain whitespaces or comments",XturtlePackage.Literals.QNAME_DEF__ID,"qnameWS");
		}
	}

	@Check
	public void checkEmptyPrefixDefined(QNameDef def) {
		if(def.getPrefix()==null && service.getQualifiedName(def)==null){
			error("no @prefix-Definition up to this point", XturtlePackage.Literals.QNAME_DEF__PREFIX,
					UNKNOWN_PREFIX,"");
		}
	}
	@Check
	public void checkEmptyPrefixDefined(QNameRef ref) {
		if(ref.getPrefix()==null && service.getQualifiedName(ref)==null){
			error("no @prefix-Definition up to this point", XturtlePackage.Literals.QNAME_REF__PREFIX,"");
		}
	}

	//check prefix definition is in line with prefix.cc
	@Check
	public void checkPrefixCC(PrefixId def) {
		Severity severity=levels.getNamespaceMismatchLevel();
		if(severity!=null){
			if(def.getId()!=null && prefixes.isKnownPrefix(def.getId())){
				List<String> expectedNs=prefixes.getUris(def.getId());
				if(!expectedNs.contains(service.getUriString(def))){
					createError(severity, "Namespace <"+expectedNs+"> is recommended by prefix.cc", XturtlePackage.Literals.PREFIX_ID__ID);
				}
			}
		}

		severity=levels.getPrefixMismatchLevel();
		if(severity!=null){
			String uri = service.getUriString(def);
			if(uri!=null && prefixes.isKnownNameSpace(uri)){
				List<String> expectedPrefixes=prefixes.getPrefixes(uri);
				if(def.getId()!=null && !expectedPrefixes.contains(def.getId())){
					createError(severity,"Prefix '"+expectedPrefixes.get(0)+"' is recommended by prefix.cc", XturtlePackage.Literals.PREFIX_ID__ID);
				}
			}
		}
	}

	@Check
	public void checkBlankNodePrefix(PrefixId def) {
		if("_".equals(def.getId())){
			error("illegal prefix definition", XturtlePackage.Literals.PREFIX_ID__ID, "blank_prefix");
		}
	}

	@Check
	public void checkBlankNodeSubject(PredicateObjectList predObj) {
		Predicate predicate = predObj.getVerb();
		if(predicate instanceof QNameRef){
			QNameRef ref = (QNameRef) predicate;
			boolean isBlankPrefix=ref.getPrefix().getId()==null && ref.getPrefix().getUri()==null;
			if(isBlankPrefix){
				error("blank node reference cannot be a subject", predicate, XturtlePackage.Literals.QNAME_REF__PREFIX);
			}
		}
	}

	@Check
	public void checkUnusedPrefix(PrefixId def) {
		Severity s=levels.getUnusedPrefixLevel();
		if(s!=null){
			if(def.getId()!=null){
				Collection<Setting> candidates = EcoreUtil.UsageCrossReferencer.find(def, def.eResource());
				if(candidates.size()==0){
					createError(s, "unused prefix", XturtlePackage.Literals.PREFIX_ID__ID);
				}
			}else{
				//TODO currently no validation for unused empty prefixes
			}
		}
	}

	@Check
	public void checkDuplicatePrefixInDirectives(Directives directives){
		Map<String,PrefixId> firstOccurence=new HashMap<String,PrefixId>();
		Set<String> duplicatePrefixes=new HashSet<String>();
		for (PrefixId prefixId : EcoreUtil2.typeSelect(directives.getDirective(), PrefixId.class)) {
			String prefix=prefixId.getId();
			if(firstOccurence.containsKey(prefix)){
				duplicatePrefixes.add(prefix);
				error("duplicate prefix id", prefixId, XturtlePackage.Literals.PREFIX_ID__ID,-1);
			}else{
				firstOccurence.put(prefix, prefixId);
			}
		}
		for (String string : duplicatePrefixes) {
			error("duplicate prefix id",firstOccurence.get(string), XturtlePackage.Literals.PREFIX_ID__ID,-1);
		}
	}

	@Check
	public void checkXSDType(StringLiteral literal){
		Severity level=levels.getXsdTypeLevel();
		if(level!=null && literal.getType()!=null){
			QualifiedName uri = service.getQualifiedName(literal.getType());
			Optional<String> errorMessage = XsdTypeValidator.getXsdError(literal.getValue(), uri);
			if(errorMessage.isPresent()){
				createError(level, errorMessage.get(), XturtlePackage.Literals.LITERAL__VALUE);
			}
		}
	}

	@Check
	public void preventRdfListPropertySubject(Resource subject){
		String subjectUri = service.getUriString(subject);
		if(linkingErrorExceptions.matchesRdfListProperty(subjectUri)){
			EStructuralFeature feature=(subject instanceof UriDef)?XturtlePackage.Literals.URI_DEF__URI:XturtlePackage.Literals.QNAME_DEF__ID;
			error("rdf list property not allowed as subject", feature);
		}
	}

	protected void createError(Severity s, String errorMEssage, EStructuralFeature feature){
		switch (s) {
		case ERROR:
			error(errorMEssage, feature);
			break;
		case WARNING:
			warning(errorMEssage, feature);
			break;
		case INFO:
			info(errorMEssage, feature);
			break;
		default:
			break;
		}
	}

	protected TurtleResourceService getService() {
		return service;
	}
}
