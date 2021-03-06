package io.webfolder.ui4j.webkit.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.html.HTMLDocument;

import com.sun.webkit.dom.DocumentImpl;

import io.webfolder.ui4j.api.dom.Document;
import io.webfolder.ui4j.api.dom.Element;
import io.webfolder.ui4j.api.dom.EventTarget;
import io.webfolder.ui4j.api.event.EventHandler;
import io.webfolder.ui4j.spi.PageContext;
import io.webfolder.ui4j.webkit.browser.WebKitHtmlParser;
import io.webfolder.ui4j.webkit.browser.WebKitPageContext;
import io.webfolder.ui4j.webkit.spi.WebKitJavaScriptEngine;
import javafx.scene.web.WebEngine;

public class WebKitDocument implements Document, EventTarget {

    private WebKitJavaScriptEngine engine;

    private PageContext context;

    private DocumentImpl document;

    private WebKitHtmlParser parser;

    public WebKitDocument(PageContext context, DocumentImpl document, WebKitJavaScriptEngine engine) {
        this.context = context;
        this.document = document;
        this.engine = engine;
        this.parser = new WebKitHtmlParser(engine);
    }

    @Override
    public Element query(String selector) {
        return ((WebKitPageContext) context).getSelectorEngine(document).query(selector);
    }

    @Override
    public List<Element> queryAll(String selector) {
        return ((WebKitPageContext) context).getSelectorEngine(document).queryAll(selector);
    }

    @Override
    public Element createElement(String tagName) {
        org.w3c.dom.Element element = document.createElement(tagName);
        return ((WebKitPageContext) context).createElement(element, this, engine);
    }

    @Override
    public void bind(String event, EventHandler handler) {
        context.getEventManager().bind(this, event, handler);
    }

    @Override
    public void unbind(String event) {
        context.getEventManager().unbind(this, event);
    }

    @Override
    public void unbind() {
        context.getEventManager().unbind(this);
    }

    @Override
    public Element getBody() {
        Element element = ((WebKitPageContext) context).createElement(document.getBody(), this, engine);
        return element;
    }

    @Override
    public Element getHead() {
        Element element = ((WebKitPageContext) context).createElement(document.getHead(), this, engine);
        return element;
    }

    public WebEngine getEngine() {
        return engine.getEngine();
    }

    @Override
    public String getTitle() {
        String title = document.getTitle();
        if (title == null || title.isEmpty()) {
            return null;
        }
        return title;
    }

    @Override
    public void setTitle(String title) {
        document.setTitle(title);
    }

    @Override
    public void removeProperty(String key) {
        document.removeMember(key);
    }

    @Override
    public Object getProperty(String key) {
        return document.getMember(key);
    }

    @Override
    public void setProperty(String key, Object value) {
        document.setMember(key, value);
    }

    @Override
    public List<Element> parseHTML(String html) {
        NodeList childNodes = parser.parse(html, (HTMLDocument) document);
        List<Element> list = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node next = childNodes.item(i);
            Element element = ((WebKitPageContext) context).createElement(next, this, engine);
            list.add(element);
        }
        if (list.isEmpty()) {
            return Collections.emptyList();
        } else {
            return list;
        }
    }

    @Override
    public void trigger(String eventType, Element element) {
        if (!(element instanceof WebKitElement)) {
            return;
        }
        Event event = document.createEvent("HTMLEvents");
        event.initEvent(eventType, true, true);
        WebKitElement elementImpl = (WebKitElement) element;
        elementImpl.getHtmlElement().dispatchEvent(event);
    }

    @Override
    public Element getElementFromPoint(int x, int y) {
        org.w3c.dom.Element w3cElement = document.elementFromPoint(x, y);
        if (w3cElement == null) {
            return null;
        }
        Element element = ((WebKitPageContext) context).createElement(w3cElement, this, engine);
        return element;
    }

    public DocumentImpl getDocument() {
        return document;
    }

    public void refreshDocument() {
        this.document = (DocumentImpl) engine.getEngine().getDocument();
    }
}
