package presto.android.gui.listener;

import com.google.common.collect.Sets;
import presto.android.Hierarchy;
import presto.android.gui.graph.NVarNode;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* Class that encapsulates the information about the listener class in the application
*  along with some additional information about the listener like eventType, handlerPrototypes, androidListenerClass
*
*  Cannot use ListenerRegistration directly.
* */
public class ListenerInstance {
    private Set<SootMethod> handlerPrototypes;
//  This is needed because the Activity need not implement the Listener interface & the event handler can have any name and need not match the method name from the interface.
    private Set<SootMethod> inlineEventHandlerMethods;
    private Set<SootMethod> eventHandlerMethods;
    private EventType eventType;

    public ListenerInstance(SootClass androidListenerClass,
                            SootClass applicationListenerClass,
                            Set<SootMethod> handlerPrototypes,
                            EventType eventType) {
        this.handlerPrototypes = handlerPrototypes;
        this.eventType = eventType;
        this.inlineEventHandlerMethods = Sets.newHashSet();
        this.eventHandlerMethods = Sets.newHashSet();
    }

    public EventType getEventType() {
        return eventType;
    }

    public Set<SootMethod> computeConcreteHandlers(SootClass listenerClass) {
        Set<SootClass> listenerTypes = Collections.unmodifiableSet(Hierarchy.v().getSubtypes(listenerClass));
        Set<SootMethod> handlersFromInterface = computeConcreteHandlers(handlerPrototypes, listenerTypes);
        eventHandlerMethods.addAll(handlersFromInterface);
        return handlersFromInterface;
    }

    public Set<SootMethod> computeConcreteHandlers(NVarNode listenerNode) {
        Set<SootClass> listenerTypes = computePossibleListenerTypesCHA(listenerNode);
        Set<SootMethod> sootMethods = computeConcreteHandlers(handlerPrototypes, listenerTypes);
        eventHandlerMethods.addAll(sootMethods);
        return sootMethods;
    }

    private Set<SootClass> computePossibleListenerTypesCHA(NVarNode listenerNode) {
        SootClass declaredListenerType =
                ((RefType)listenerNode.l.getType()).getSootClass();
        return Collections.unmodifiableSet(Hierarchy.v().getSubtypes(declaredListenerType));
    }

    public Set<SootMethod> getAllEventHandlers () {
        Set<SootMethod> allEventHandlers = new HashSet<SootMethod>();
        allEventHandlers.addAll(eventHandlerMethods);
        allEventHandlers.addAll(inlineEventHandlerMethods);
        return allEventHandlers;
    }

    public void recordInlineEventHandler(Set<SootMethod> methods) {
        this.inlineEventHandlerMethods.addAll(methods);
    }

    private Set<SootMethod> computeConcreteHandlers(Set<SootMethod> handlerPrototypes, Set<SootClass> listenerTypes) {
        Set<SootMethod> handlers = new HashSet<SootMethod>();
        for (SootClass possibleListenerType : listenerTypes) {
            for (SootMethod prototype : handlerPrototypes) {
                String prototypeSubsig = prototype.getSubSignature();
                SootClass matchedClass = Hierarchy.v().matchForVirtualDispatch(
                        prototypeSubsig, possibleListenerType);
                if (matchedClass != null && matchedClass.isApplicationClass()
                        && ListenerSpecification.v().isListenerType(matchedClass)) {
                    SootMethod h = matchedClass.getMethod(prototypeSubsig);
                    if (h.isConcrete()) {
                        handlers.add(h);
                    }
                }
            }
        }
        return handlers;
    }

}
