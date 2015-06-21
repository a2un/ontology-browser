package org.coode.owl.mngr.impl;

import org.coode.owl.mngr.ServerOptionsAdapter;
import org.coode.owl.mngr.ServerProperties;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ServerOptionsAdapterImpl<E extends Enum> implements ServerOptionsAdapter<E> {

    protected ServerProperties delegate;

    public ServerOptionsAdapterImpl(ServerProperties delegate) {
        this.delegate = delegate;
    }

    // share the delegate
    public ServerOptionsAdapterImpl(ServerOptionsAdapterImpl anotherAdapter) {
        this.delegate = anotherAdapter.delegate;
    }


    public void addPropertyChangeListener(PropertyChangeListener l) {
        delegate.addPropertyChangeListener(l);
    }


    public void removePropertyChangeListener(PropertyChangeListener l) {
        delegate.removePropertyChangeListener(l);
    }


    public void save(OutputStream out) throws IOException {
        delegate.save(out);
    }

    public void load(InputStream in) throws IOException {
        delegate.load(in);
    }


    public void addDeprecatedNames(Map<String, String> old2NewNames) {
        delegate.addDeprecatedNames(old2NewNames);
    }


    public void setBoolean(E key, boolean b) {
        delegate.set(key.name(), Boolean.toString(b));
    }


    public String get(E key) {
        return delegate.get(key.name());
    }


    public boolean set(E key, String value) {
        return delegate.set(key.name(), value);
    }

    @Override
    public Map<String, String> getAll() {
        return delegate.getAll();
    }


    public boolean isSet(E key) {
        return delegate.isSet(key.name());
    }

    public URL getURL(E key) throws MalformedURLException {
        return delegate.getURL(key.name());
    }


    public void remove(E key) {
        delegate.remove(key.name());
    }


    public void setAllowedValues(E key, List<String> values) {
        delegate.setAllowedValues(key.name(), values);
    }


    public List<String> getAllowedValues(E key) {
        return delegate.getAllowedValues(key.name());
    }
}