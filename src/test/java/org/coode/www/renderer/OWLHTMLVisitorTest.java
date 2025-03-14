package org.coode.www.renderer;

import org.coode.html.url.URLScheme;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.ShortFormProvider;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class OWLHTMLVisitorTest {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<String[]> data() {
        return Arrays.asList(new String[][]{
                {"With fragment", "http://example.org/thing#Name", "http://<wbr>example.org/<wbr>thing#<b>Name</b>"},
                {"Without fragment", "http://example.org/thing/", "http://<wbr>example.org/<wbr>thing/"}
        });
    }

    @Parameterized.Parameter
    public String name;

    @Parameterized.Parameter(1)
    public String fInput;

    @Parameterized.Parameter(2)
    public String fExpected;

    // TODO too many dependencies = smell
    private OWLHTMLVisitor visitor = new OWLHTMLVisitor(
            mock(ShortFormProvider.class),
            mock(OntologyShortFormProvider.class),
            mock(URLScheme.class),
            Collections.emptySet(),
            mock(OWLOntology.class),
            Optional.empty());

    @Test
    public void test() {
        final StringWriter out = new StringWriter();
        visitor.setWriter(new PrintWriter(out));
        visitor.visit(IRI.create(fInput));
        assertThat(out.toString(), equalTo(fExpected));
    }
}
