/*******************************************************************************
 * Copyright (c) 2013 AKSW Xturtle Project, itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
/*
 * generated by Xtext
 */
package de.itemis.tooling.xturtle.ui;

import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.resource.containers.IAllContainersState;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.actions.IActionContributor;
import org.eclipse.xtext.ui.editor.autoedit.AbstractEditStrategyProvider;
import org.eclipse.xtext.ui.editor.findrefs.IReferenceFinder;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionProvider;
import org.eclipse.xtext.ui.editor.hover.IEObjectHover;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.hyperlinking.IHyperlinkHelper;
import org.eclipse.xtext.ui.editor.hyperlinking.OpenDeclarationHandler;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;
import org.eclipse.xtext.ui.editor.syntaxcoloring.AbstractAntlrTokenToAttributeIdMapper;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.name.Names;

import de.itemis.tooling.xturtle.CustomXturtleLexer;
import de.itemis.tooling.xturtle.resource.TurtleIndexUserDataNamesProvider;
import de.itemis.tooling.xturtle.ui.autoedit.TurtleAutoEditStrategyProvider;
import de.itemis.tooling.xturtle.ui.autoedit.TurtleNewLineAutoedit;
import de.itemis.tooling.xturtle.ui.contentassist.CustomXturtleContentassistLexer;
import de.itemis.tooling.xturtle.ui.contentassist.TurtleLiteralsLanguages;
import de.itemis.tooling.xturtle.ui.contentassist.antlr.internal.InternalXturtleLexer;
import de.itemis.tooling.xturtle.ui.findrefs.TurtleReferenceFinder;
import de.itemis.tooling.xturtle.ui.folding.TurtleFoldingActionContributor;
import de.itemis.tooling.xturtle.ui.folding.TurtleFoldingRegionProvider;
import de.itemis.tooling.xturtle.ui.hover.TurtleEObjectHover;
import de.itemis.tooling.xturtle.ui.hover.TurtleEObjectHoverProvider;
import de.itemis.tooling.xturtle.ui.hyperlinking.TurtleHyperlinkHelper;
import de.itemis.tooling.xturtle.ui.hyperlinking.TurtleOpenDeclarationHandler;
import de.itemis.tooling.xturtle.ui.preferences.TurtlePreferenceBasedLiteralsLanguages;
import de.itemis.tooling.xturtle.ui.preferences.TurtlePreferenceBasedNoLinkingValidationUriPrefixes;
import de.itemis.tooling.xturtle.ui.preferences.TurtlePreferenceBasedUserDataNamesProvider;
import de.itemis.tooling.xturtle.ui.preferences.TurtlePreferenceBasedValidationSeverityLevels;
import de.itemis.tooling.xturtle.ui.syntaxcoloring.TurtleHighlightingConfig;
import de.itemis.tooling.xturtle.ui.syntaxcoloring.TurtleHighlightingMapper;
import de.itemis.tooling.xturtle.ui.syntaxcoloring.TurtleSemanticHighlighter;
import de.itemis.tooling.xturtle.ui.templates.TurtleTemplateContextTypeRegistry;
import de.itemis.tooling.xturtle.ui.validation.XturtleUIJavaValidator;
import de.itemis.tooling.xturtle.validation.TurtleFixedSeverityLevels;
import de.itemis.tooling.xturtle.validation.TurtleNoLinkingValidationUriPrefixes;
import de.itemis.tooling.xturtle.validation.TurtleValidationSeverityLevels;

/**
 * Use this class to register components to be used within the IDE.
 */
public class XturtleUiModule extends de.itemis.tooling.xturtle.ui.AbstractXturtleUiModule {
	public XturtleUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}

	@Override
	public void configureHighlightingLexer(com.google.inject.Binder binder) {
		binder.bind(org.eclipse.xtext.parser.antlr.Lexer.class).annotatedWith(com.google.inject.name.Names.named(org.eclipse.xtext.ui.LexerUIBindings.HIGHLIGHTING)).to(CustomXturtleLexer.class);
	}

	/**
	 * Override that injects a wrapper for the external lexer used by the main parser.
	 * contributed by org.eclipse.xtext.generator.parser.antlr.ex.ca.ContentAssistParserGeneratorFragment.
	 * 
	 * Without this override, a default generated lexer will be used and this lexer will not be correct as
	 * PP parsing requires an external lexer. The binding reuses the main lexer.
	 */
	@Override
	public void configureContentAssistLexer(com.google.inject.Binder binder) {
		binder.bind(org.eclipse.xtext.ui.editor.contentassist.antlr.internal.Lexer.class).annotatedWith(
			com.google.inject.name.Names.named(org.eclipse.xtext.ui.LexerUIBindings.CONTENT_ASSIST)).to(
			CustomXturtleContentassistLexer.class);
	}

	/**
	 * Override that injects a wrapper for the external lexer used by the main parser.
	 * contributed by org.eclipse.xtext.generator.parser.antlr.ex.ca.ContentAssistParserGeneratorFragment
	 */
	@Override
	public void configureContentAssistLexerProvider(com.google.inject.Binder binder) {
		binder.bind(InternalXturtleLexer.class).toProvider(
			org.eclipse.xtext.parser.antlr.LexerProvider.create(CustomXturtleContentassistLexer.class));
	}

	public Class <? extends AbstractAntlrTokenToAttributeIdMapper> bindTokenMapper(){
		return TurtleHighlightingMapper.class;
	}

	public Class<? extends IHyperlinkHelper> bindIHyperlinkHelper() {
		return TurtleHyperlinkHelper.class;
	}

	public Class<? extends OpenDeclarationHandler> bindOpenDeclarationHandler() {
		return TurtleOpenDeclarationHandler.class;
	}

	public Class<? extends IHighlightingConfiguration> bindHighlightingConfig(){
		return TurtleHighlightingConfig.class;
	}

	public Class<? extends ISemanticHighlightingCalculator> bindSemanticHighlighter(){
		return TurtleSemanticHighlighter.class;
	}

	@Override
	public Class<? extends IEObjectHover> bindIEObjectHover() {
		return TurtleEObjectHover.class;
	}

	public Class<? extends IEObjectHoverProvider> bindHoverProvider(){
		return TurtleEObjectHoverProvider.class;
	}

	public Class<? extends DefaultIndentLineAutoEditStrategy> bindAutoIndentStrategy(){
		return TurtleNewLineAutoedit.class;
	}

	public Class<? extends IReferenceFinder> bindReferenceFinder(){
		return TurtleReferenceFinder.class;
	}
	
	@Override
	public Provider<IAllContainersState> provideIAllContainersState() {
		return org.eclipse.xtext.ui.shared.Access.getWorkspaceProjectsState();
//		return org.eclipse.xtext.ui.shared.Access.getJavaProjectsState();
	}
	
	@Override
	public Class<? extends AbstractEditStrategyProvider> bindAbstractEditStrategyProvider() {
		return TurtleAutoEditStrategyProvider.class;
	}

	
	//Folding Start
	@Override
	public void configureBracketMatchingAction(Binder binder) {
		//actually we want to override the first binding only...
		binder.bind(IActionContributor.class).annotatedWith(Names.named("foldingActionGroup")).to( //$NON-NLS-1$
				TurtleFoldingActionContributor.class);
		binder.bind(IActionContributor.class).annotatedWith(Names.named("bracketMatcherAction")).to( //$NON-NLS-1$
				org.eclipse.xtext.ui.editor.bracketmatching.GoToMatchingBracketAction.class);
		binder.bind(IPreferenceStoreInitializer.class).annotatedWith(Names.named("bracketMatcherPrefernceInitializer")) //$NON-NLS-1$
				.to(org.eclipse.xtext.ui.editor.bracketmatching.BracketMatchingPreferencesInitializer.class);
		binder.bind(IActionContributor.class).annotatedWith(Names.named("selectionActionGroup")).to( //$NON-NLS-1$
				org.eclipse.xtext.ui.editor.selection.AstSelectionActionContributor.class);
	}

	public Class<? extends XtextEditor> bindEditor() {
		return TurtleXtextEditor.class;
	}
	public Class<? extends IFoldingRegionProvider> bindFoldingRegionProvider() {
		return TurtleFoldingRegionProvider.class;
	}
	//Folding End

	// contributed by org.eclipse.xtext.ui.generator.projectWizard.SimpleProjectWizardFragment
	public Class<? extends org.eclipse.xtext.ui.wizard.IProjectCreator> bindIProjectCreator() {
		return de.itemis.tooling.xturtle.ui.wizard.TurtleProjectCreator.class;
	}

	@Override
	public Class<? extends ContextTypeRegistry> bindContextTypeRegistry() {
		return TurtleTemplateContextTypeRegistry.class;
	}

	public Class<? extends TurtleIndexUserDataNamesProvider> bindUserDataNamesProvider() {
		return TurtlePreferenceBasedUserDataNamesProvider.class;
	}

	public Class<? extends TurtleValidationSeverityLevels> bindSeverityLevels() {
		return TurtlePreferenceBasedValidationSeverityLevels.class;
	}

	public Class<? extends TurtleNoLinkingValidationUriPrefixes> bindIgnoreLinkingUriPrefixes() {
		return TurtlePreferenceBasedNoLinkingValidationUriPrefixes.class;
	}

	public Class<? extends TurtleLiteralsLanguages> bindCALanguages() {
		return TurtlePreferenceBasedLiteralsLanguages.class;
	}

	// contributed by org.eclipse.xtext.generator.validation.JavaValidatorFragment
	@org.eclipse.xtext.service.SingletonBinding(eager=true)	public Class<? extends de.itemis.tooling.xturtle.validation.XturtleJavaValidator> bindXturtleJavaValidator() {
		return XturtleUIJavaValidator.class;
	}

}
