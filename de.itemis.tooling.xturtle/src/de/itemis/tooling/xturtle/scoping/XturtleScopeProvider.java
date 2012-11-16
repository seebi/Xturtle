/*
 * generated by Xtext
 */
package de.itemis.tooling.xturtle.scoping;

import java.util.List;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.eclipse.xtext.util.IResourceScopeCache;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;

import de.itemis.tooling.xturtle.xturtle.PrefixId;
import de.itemis.tooling.xturtle.xturtle.TurtleDoc;

/**
 * This class contains custom scoping description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping
 * on how and when to use it 
 *
 */
public class XturtleScopeProvider extends AbstractDeclarativeScopeProvider {

	@Inject
	IResourceScopeCache cache;
	//only the prefixes defined within the model are visible
	//TODO maybe link prefix directly
	IScope scope_QNameDef_prefix(TurtleDoc m, EReference ref){
		return getNamespacePrefixScope(m);
	}
	IScope scope_QNameRef_prefix(TurtleDoc m, EReference ref){
		return getNamespacePrefixScope(m);
	}

	//adapt Linker in order to get the correct target!!
	private IScope getNamespacePrefixScope(final TurtleDoc m) {
		List<PrefixId> prefixIds = cache.get("prefixIds", m.eResource(), new Provider<List<PrefixId>>() {
			public List<PrefixId> get() {
				return EcoreUtil2.getAllContentsOfType(m, PrefixId.class);
			}
		});
		return Scopes.scopeFor(prefixIds, new Function<PrefixId, QualifiedName>() {
			public QualifiedName apply(PrefixId def){
					return QualifiedName.create("",Optional.fromNullable(def.getId()).or(""));
			}
		}, IScope.NULLSCOPE);
	}
}
