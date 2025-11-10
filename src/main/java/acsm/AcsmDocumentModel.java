package acsm;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

// Assumed imports for AcsmModel contract
import acsm.AcsmModel.Permission;
import acsm.AcsmModel.Property;

// Import XmlUtils from the provided file's package structure (util)
import util.XmlUtils; //

class AcsmDocumentModel
{
    private final Document document;
    
    private static final DateTimeFormatter ISO_8601 = DateTimeFormatter.ISO_DATE_TIME;
    
    // --- CONSTRUCTORS ---
    
    /**
     * Initializes the internal data store by saving a reference to the passed Document.
     * @param document The backing XML Document (assumed to satisfy the ACSM schema).
     */
    public AcsmDocumentModel( Document document )
    {
        // Preferred: save a reference to the passed object.
        this.document = document;
        if ( !this.document.getDocumentElement().getLocalName().equals( "fulfillmentToken" ) ) {
            throw new IllegalArgumentException( "Document root element is not 'fulfillmentToken'." );
        }
    }
    
    /**
     * Private constructor used by the Builder to instantiate the final object.
     */
    private AcsmDocumentModel( Document document, boolean isBuilderCall )
    {
        this.document = document;
    }
    
    // --- INTERNAL UTILITY (Wrappers for Getters/Setters using XmlUtils) ---
    
    private String getStringValue( String xPathExpression )
    {
        try
        {
            // The XmlUtils.getString(Node, String) method logic is assumed to handle the Document as the context node.
            return XmlUtils.getString( document, xPathExpression ); //
        }
        catch ( XPathExpressionException e )
        {
            return null;
        }
    }
    
    private Node getNode( String xPathExpression )
    {
        try
        {
            return XmlUtils.getNode( document, xPathExpression, false ); //
        }
        catch ( XPathExpressionException e )
        {
            return null;
        }
    }
    
    // --- ACSM Model Contract Implementation (Getters/Setters) ---
    public Document getDocument()
    {
        return document;
    }
    
    // New required getter
    public Element getFulfillmentToken()
    {
        return document.getDocumentElement();
    }
    
    public String getContent( String nodeName )
    {
        return getStringValue( "/adept:fulfillmentToken/adept:" + nodeName + "/text()" );
    }
    
    public String getDistributor()
    {
        return getStringValue( "/adept:fulfillmentToken/adept:distributor/text()" );
    }
    
    public String getOperatorURL()
    {
        return getStringValue( "/adept:fulfillmentToken/adept:operatorURL/text()" );
    }
    
    public String getTransaction()
    {
        return getStringValue( "/adept:fulfillmentToken/adept:transaction/text()" );
    }
    
    public String getPurchase()
    {
        return getStringValue( "/adept:fulfillmentToken/adept:purchase/text()" );
    }
    
    public String setPurchase( String creationDate )
    {
        Node node = getNode( "/adept:fulfillmentToken/adept:purchase" );
        if ( node != null ) {
            node.setTextContent( creationDate );
        }
        return creationDate;
    }
    
    public String setPurchase( ZonedDateTime date)
    {
        String creationDate = date.format( ISO_8601 );
        return setPurchase( creationDate );
    }
    
    public String getExpiration()
    {
        return getStringValue( "/adept:fulfillmentToken/adept:expiration/text()" );
    }
    
    public String setExpiration( String expiration )
    {
        Node node = getNode( "/adept:fulfillmentToken/adept:expiration" );
        if ( node != null ) {
            node.setTextContent( expiration );
        }
        return expiration;
    }
    
    public String setExpiration( ZonedDateTime expiration)
    {
        String expirationDate = expiration.format( ISO_8601 );
        return setExpiration( expirationDate );
    }
    
    public String getResource()
    {
        return getStringValue( "/adept:fulfillmentToken/adept:resourceItemInfo/adept:resource/text()" );
    }
    
    public String setResourceUUID( UUID uuid )
    {
        String urn = "urn:uuid:" + uuid.toString();
        Node node = getNode( "/adept:fulfillmentToken/adept:resourceItemInfo/adept:resource" );
        if ( node != null ) {
            node.setTextContent( urn );
        }
        return urn;
    }
    
    public UUID getResourceUUID()
    {
        String urn = getResource();
        if ( urn != null && urn.startsWith( "urn:uuid:" ) ) {
            return UUID.fromString( urn.substring( 9 ) );
        }
        return null;
    }
    
    public String getResourceItem()
    {
        return getStringValue( "/adept:fulfillmentToken/adept:resourceItemInfo/adept:resourceItem/text()" );
    }
    
    public String setResourceItem( String resourceItem )
    {
        Node node = getNode( "/adept:fulfillmentToken/adept:resourceItemInfo/adept:resourceItem" );
        if ( node != null ) {
            node.setTextContent( resourceItem );
        }
        return resourceItem;
    }
    
    public Property getMetadata( String key )
    {
        // Search for a metadata element by key using local-name()
        Node node = getNode( "/adept:fulfillmentToken/adept:resourceItemInfo/adept:metadata/*[local-name()='" + key + "']" );
        
        if ( node instanceof Element element ) {
            // Use XmlUtils.getAttributes(Element) for the AttributeBag
            return new AcsmModel.Property( node.getLocalName(),
                    new AcsmModel.AttributeBag( node.getTextContent(), XmlUtils.getAttributes(element) ) ); //
        }
        return null;
    }
    
    // Placeholder implementations for complex list getters
    public List<Property> getMetadata()
    {
        return new ArrayList<>();
    }
    
    public List<Permission> getPermissions()
    {
        return new ArrayList<>();
    }
    
    public String getSrc()
    {
        return getStringValue( "/adept:fulfillmentToken/adept:resourceItemInfo/adept:src/text()" );
    }
    
    public void setSrc( String src )
    {
        Node node = getNode( "/adept:fulfillmentToken/adept:resourceItemInfo/adept:src" );
        if ( node != null ) {
            node.setTextContent( src );
        }
    }
    
    public String getDownloadType()
    {
        return getStringValue( "/adept:fulfillmentToken/adept:resourceItemInfo/adept:downloadType/text()" );
    }
    
    public void setDownloadType( String downloadType )
    {
        Node node = getNode( "/adept:fulfillmentToken/adept:resourceItemInfo/adept:downloadType" );
        if ( node != null ) {
            node.setTextContent( downloadType );
        }
    }
    
    public String getUserId()
    {
        return getStringValue( "/adept:fulfillmentToken/adept:userId/text()" );
    }
    
    public void setUserId( String userId )
    {
        Node node = getNode( "/adept:fulfillmentToken/adept:userId" );
        if ( node != null ) {
            node.setTextContent( userId );
        }
    }
    
    public String getFulfillmentType()
    {
        // Attribute access: use XPath /@attribute
        return getStringValue( "/adept:fulfillmentToken/@fulfillmentType" );
    }
    
    public void setFulfillmentType( String fulfillmentType )
    {
        Node node = getNode( "/adept:fulfillmentToken" );
        if ( node instanceof Element element ) {
            element.setAttribute( "fulfillmentType", fulfillmentType );
        }
    }
    
    public String getAuth()
    {
        return getStringValue( "/adept:fulfillmentToken/@auth" );
    }
    
    public void setAuth( String auth )
    {
        Node node = getNode( "/adept:fulfillmentToken" );
        if ( node instanceof Element element ) {
            element.setAttribute( "auth", auth );
        }
    }
    
    public String getHmac()
    {
        return getStringValue( "/adept:fulfillmentToken/adept:hmac/text()" );
    }
    
    public void setHmac( String hmac )
    {
        Node node = getNode( "/adept:fulfillmentToken/adept:hmac" );
        if ( node != null ) {
            node.setTextContent( hmac );
        }
    }
    
    // --- CONCRETE STAGED BUILDER IMPLEMENTATION ---
    
    /**
     * Top-level concrete builder class. It uses concrete static inner classes to enforce the staged build.
     */
    public static class DocumentModelBuilder
    {
        private Document document;
        private final Map<String, String> data = new HashMap<>();
        
        private DocumentModelBuilder() {
            try {
                // Use XmlUtils.createDocumentBuilder() to get a namespace-aware builder
                DocumentBuilder dBuilder = XmlUtils.createDocumentBuilder(); //
                this.document = dBuilder.newDocument();
                
                // Create the root element with the Adept namespace (XmlUtils.ADEPT_NS)
                Element root = this.document.createElementNS( XmlUtils.ADEPT_NS, "fulfillmentToken" ); //
                root.setPrefix( "adept" );
                this.document.appendChild( root );
                
                // Add resourceItemInfo placeholder
                Element resourceItemInfo = this.document.createElementNS( XmlUtils.ADEPT_NS, "resourceItemInfo" ); //
                resourceItemInfo.setPrefix( "adept" );
                root.appendChild( resourceItemInfo );
                
            } catch ( Exception e ) {
                throw new RuntimeException( "Failed to initialize XML Document for Builder.", e );
            }
        }
        
        /** The starting method for the staged build process. */
        public static DistributorStage start() {
            return new DistributorStage( new DocumentModelBuilder() );
        }
        
        // Internal helper to create and append a namespaced element with text content
        private void appendElement( Element parent, String name, String content ) {
            Element element = document.createElementNS( XmlUtils.ADEPT_NS, name ); //
            element.setPrefix( "adept" );
            element.setTextContent( content );
            parent.appendChild( element );
        }
        
        // Populates the Document with the mandatory fields collected by the staged builder
        private void populateMandatoryElements() {
            Element root = document.getDocumentElement();
            // Use XmlUtils.getNode(Document, String, boolean) to reliably get the resourceItemInfo node
            Node resourceItemInfoNode = null;
            try {
                resourceItemInfoNode = XmlUtils.getNode( document, "/adept:fulfillmentToken/adept:resourceItemInfo", true ); //
            } catch ( XPathExpressionException e ) {
                throw new RuntimeException( "Internal builder error: resourceItemInfo node not found.", e );
            }
            Element resourceItemInfo = (Element) resourceItemInfoNode;
            
            // Core Top-Level Elements
            appendElement( root, "distributor", data.get( "distributor" ) );
            appendElement( root, "operatorURL", data.get( "operatorURL" ) );
            appendElement( root, "transaction", data.get( "transaction" ) );
            appendElement( root, "purchase", data.get( "purchase" ) );
            appendElement( root, "expiration", data.get( "expiration" ) );
            
            // Resource Item Info Elements
            appendElement( resourceItemInfo, "resource", data.get( "resourceId" ) );
            
            // Placeholder for remaining mandatory elements
            appendElement( resourceItemInfo, "resourceItem", "1" );
            appendElement( root, "hmac", "" );
        }
        
        // --- STAGED BUILDER CLASSES ---
        
        public static class DistributorStage {
            private final DocumentModelBuilder builder;
            private DistributorStage( DocumentModelBuilder builder ) { this.builder = builder; }
            public OperatorURLStage setDistributor( String distributor ) {
                builder.data.put( "distributor", distributor );
                return new OperatorURLStage( builder );
            }
        }
        
        public static class OperatorURLStage {
            private final DocumentModelBuilder builder;
            private OperatorURLStage( DocumentModelBuilder builder ) { this.builder = builder; }
            public TransactionStage setOperatorURL( String operatorURL ) {
                builder.data.put( "operatorURL", operatorURL );
                return new TransactionStage( builder );
            }
        }
        
        public static class TransactionStage {
            private final DocumentModelBuilder builder;
            private TransactionStage( DocumentModelBuilder builder ) { this.builder = builder; }
            public PurchaseStage setTransaction( String transaction ) {
                builder.data.put( "transaction", transaction );
                return new PurchaseStage( builder );
            }
        }
        
        public static class PurchaseStage {
            private final DocumentModelBuilder builder;
            private PurchaseStage( DocumentModelBuilder builder ) { this.builder = builder; }
            public ExpirationStage setPurchase( ZonedDateTime purchase ) {
                builder.data.put( "purchase", purchase.format( ISO_8601 ) );
                return new ExpirationStage( builder );
            }
        }
        
        public static class ExpirationStage {
            private final DocumentModelBuilder builder;
            private ExpirationStage( DocumentModelBuilder builder ) { this.builder = builder; }
            public ResourceIdStage setExpiration( ZonedDateTime expiration ) {
                builder.data.put( "expiration", expiration.format( ISO_8601 ) );
                return new ResourceIdStage( builder );
            }
        }
        
        public static class ResourceIdStage {
            private final DocumentModelBuilder builder;
            private ResourceIdStage( DocumentModelBuilder builder ) { this.builder = builder; }
            public OptionalStage setResourceId( UUID resourceId ) {
                builder.data.put( "resourceId", "urn:uuid:" + resourceId.toString() );
                builder.populateMandatoryElements();
                return new OptionalStage( builder );
            }
        }
        
        public static class OptionalStage {
            private final DocumentModelBuilder builder;
            
            private OptionalStage( DocumentModelBuilder builder ) {
                this.builder = builder;
            }
            
            // --- OPTIONAL SETTERS (Update the XML Document directly) ---
            
            private void setElementText( String elementName, String content ) {
                // Use XmlUtils.getNode(Document, String, boolean) to find the node
                Node node = null;
                try {
                    node = XmlUtils.getNode( builder.document, elementName, false ); //
                } catch ( XPathExpressionException e ) {
                    // Ignore, node not found
                }
                if ( node != null ) {
                    node.setTextContent( content );
                }
            }
            
            // These setters modify the document's backing store directly
            public OptionalStage setMetadata( List<Property> metadata ) { return this; }
            public OptionalStage setPermissions( List<Permission> permissions ) { return this; }
            
            public OptionalStage setResourceItem( String resourceItem ) {
                setElementText( "/adept:fulfillmentToken/adept:resourceItemInfo/adept:resourceItem", resourceItem );
                return this;
            }
            
            public OptionalStage setSrc( String src ) {
                setElementText( "/adept:fulfillmentToken/adept:resourceItemInfo/adept:src", src );
                return this;
            }
            
            public OptionalStage setDownloadType( String downloadType ) {
                setElementText( "/adept:fulfillmentToken/adept:resourceItemInfo/adept:downloadType", downloadType );
                return this;
            }
            
            public OptionalStage setUserId( String userId ) {
                setElementText( "/adept:fulfillmentToken/adept:userId", userId );
                return this;
            }
            
            public OptionalStage setFulfillmentType( String fulfillmentType ) {
                builder.document.getDocumentElement().setAttribute( "fulfillmentType", fulfillmentType );
                return this;
            }
            
            public OptionalStage setAuth( String auth ) {
                builder.document.getDocumentElement().setAttribute( "auth", auth );
                return this;
            }
            
            public OptionalStage setHmac( String hmac ) {
                setElementText( "/adept:fulfillmentToken/adept:hmac", hmac );
                return this;
            }
            
            /** The final method that creates the concrete AcsmDocumentModel object. */
            public AcsmDocumentModel build() {
                return new AcsmDocumentModel( builder.document, true );
            }
        }
    }
}