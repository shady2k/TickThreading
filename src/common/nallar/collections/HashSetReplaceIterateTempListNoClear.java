package nallar.collections;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import nallar.tickthreading.util.BooleanThreadLocal;

public class HashSetReplaceIterateTempListNoClear<T> extends HashSet<T> {
	private volatile boolean defer = false;
	private final LinkedList<T> deferred = new LinkedList<T>();
	private static final Iterator emptyIterator = Collections.emptyList().iterator();
	private final BooleanThreadLocal noDefer = new BooleanThreadLocal();

	@Override
	public synchronized boolean add(T t) {
		if (defer) {
			return !contains(t) && deferred.add(t);
		} else {
			return super.add(t);
		}
	}

	@Override
	public synchronized Iterator<T> iterator() {
		if (defer) {
			noDefer.set(true);
			return emptyIterator;
		}
		defer = true;
		return super.iterator();
	}

	@Override
	public synchronized void clear() {
		if (noDefer.get()) {
			noDefer.set(false);
			return;
		}
		defer = false;
		addAll(deferred);
		deferred.clear();
	}
}
