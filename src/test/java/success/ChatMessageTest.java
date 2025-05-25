package success;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Time to test our ChatMessage class!
// We want to make sure messages are created correctly, validated, hashed, and "sent".
public class ChatMessageTest {

    // Some standard details for our test messages.
    private final String SENDER_PHONE = "+27710000001";
    private final String RECIPIENT_PHONE = "+27720000002";
    private final String VALID_PAYLOAD = "Hi there, this is a test message!";
    private final String PAYLOAD_EXACT_LIMIT = "a".repeat(250);
    private final String PAYLOAD_TOO_LONG = "a".repeat(251);


    // Before each test, let's reset any static stuff in ChatMessage so tests don't affect each other.
    @BeforeEach
    void setUp() {
        ChatMessage.resetMessageCounterForTesting();
    }

    @Test
    void testChatMessageCreation_NewMessage() {
        ChatMessage msg = new ChatMessage(SENDER_PHONE, RECIPIENT_PHONE, VALID_PAYLOAD);

        assertNotNull(msg.getId(), "Message ID should not be null.");
        assertEquals(10, msg.getId().length(), "Message ID should be 10 characters long.");
        assertTrue(msg.checkMessageID(msg.getId()), "Generated Message ID should be valid.");
        assertEquals(SENDER_PHONE, msg.getSender());
        assertEquals(RECIPIENT_PHONE, msg.getRecipient());
        assertEquals(VALID_PAYLOAD, msg.getPayload());
        assertEquals(0, msg.getIndex(), "New message index should be 0.");
        assertEquals("", msg.getHash(), "New message hash should be empty.");
        assertTrue(msg.getGeneratedIdNotification().contains(msg.getId()), "ID notification should contain the ID.");
    }

    @Test
    void testChatMessageCreation_LoadedMessage() {
        // This tests the package-private constructor used for loading from storage.
        ChatMessage msg = new ChatMessage("1234567890", SENDER_PHONE, RECIPIENT_PHONE, VALID_PAYLOAD, 5, "AB:5:HASHVALUE");
        assertEquals("1234567890", msg.getId());
        assertEquals(SENDER_PHONE, msg.getSender());
        assertEquals(RECIPIENT_PHONE, msg.getRecipient());
        assertEquals(VALID_PAYLOAD, msg.getPayload());
        assertEquals(5, msg.getIndex());
        assertEquals("AB:5:HASHVALUE", msg.getHash());
    }

    // --- Validation Tests ---
    @Test
    void testValidatePayloadLength() {
        ChatMessage msg = new ChatMessage(SENDER_PHONE, RECIPIENT_PHONE, "test"); // Payload doesn't matter for this static-like method call here
        assertEquals("Message ready to send.", msg.validatePayloadLength(VALID_PAYLOAD));
        assertEquals("Message ready to send.", msg.validatePayloadLength(PAYLOAD_EXACT_LIMIT));
        assertTrue(msg.validatePayloadLength(PAYLOAD_TOO_LONG).startsWith("Message exceeds 250 characters by 1"), "Too long payload error message incorrect.");
        assertTrue(msg.validatePayloadLength(null).contains("exceeds 250 characters by -250"), "Null payload error message incorrect.");
    }

    @Test
    void testValidateRecipientNumber() {
        ChatMessage msg = new ChatMessage(SENDER_PHONE, RECIPIENT_PHONE, "test");
        assertEquals("Cell phone number successfully captured.", msg.validateRecipientNumber(RECIPIENT_PHONE));
        assertTrue(msg.validateRecipientNumber("0711234567").contains("incorrectly formatted"), "Invalid phone format error message incorrect.");
        assertTrue(msg.validateRecipientNumber(null).contains("incorrectly formatted"), "Null phone error message incorrect.");
    }

    // --- Hashing Test ---
    @Test
    void testCreateMessageHash_ExpectedFormat() {
        ChatMessage msg = new ChatMessage(SENDER_PHONE, RECIPIENT_PHONE, "doesn't matter here");
        // Test Case 1 from spec: ID "00...", index 0, payload "Hi Mike, can you join us for dinner tonight" -> "00:0:HITONIGHT"
        assertEquals("00:0:HITONIGHT", msg.createMessageHash("0012345678", 0, "Hi Mike, can you join us for dinner tonight"));
        assertEquals("XY:10:HELLOWORLD", msg.createMessageHash("XY00000000", 10, "  Hello   World  ")); // Test with spaces
        assertEquals("CD:2:", msg.createMessageHash("CD00000000", 2, "   ")); // Empty payload after trim
    }

    // --- Sending Message Tests ---
    @Test
    void testSentMessage_Successful() {
        ChatMessage msg = new ChatMessage(SENDER_PHONE, RECIPIENT_PHONE, VALID_PAYLOAD);
        assertEquals("Message successfully sent.", msg.sentMessage());
        assertEquals(1, msg.getIndex(), "Index should be 1 after first send.");
        assertFalse(msg.getHash().isEmpty(), "Hash should be generated after sending.");
        assertEquals(1, ChatMessage.returnTotalMessages(), "Total sent messages should be 1.");
        assertTrue(msg.printMessages().contains(msg.getId()), "printMessages should contain the sent message ID.");
    }

    @Test
    void testSentMessage_Failure_InvalidRecipient() {
        ChatMessage msg = new ChatMessage(SENDER_PHONE, "invalid-phone", VALID_PAYLOAD);
        assertEquals("Failed to send message: Invalid recipient", msg.sentMessage());
        assertEquals(0, ChatMessage.returnTotalMessages()); // Counter should not increment.
    }

    @Test
    void testSentMessage_Failure_PayloadTooLong() {
        ChatMessage msg = new ChatMessage(SENDER_PHONE, RECIPIENT_PHONE, PAYLOAD_TOO_LONG);
        assertEquals("Failed to send message: Payload too long", msg.sentMessage());
        assertEquals(0, ChatMessage.returnTotalMessages());
    }
    
    @Test
    void testSentMessage_Failure_EmptyPayload() {
        ChatMessage msg = new ChatMessage(SENDER_PHONE, RECIPIENT_PHONE, " "); // Empty after trim
        assertEquals("Failed to send message: Message content cannot be empty", msg.sentMessage());
        assertEquals(0, ChatMessage.returnTotalMessages());
    }

    @Test
    void testSentMessage_MultipleMessages_CorrectIndexingAndCount() {
        ChatMessage msg1 = new ChatMessage(SENDER_PHONE, RECIPIENT_PHONE, "First message");
        msg1.sentMessage();

        ChatMessage msg2 = new ChatMessage(SENDER_PHONE, RECIPIENT_PHONE, "Second message");
        msg2.sentMessage();

        assertEquals(1, msg1.getIndex());
        assertEquals(2, msg2.getIndex());
        assertEquals(2, ChatMessage.returnTotalMessages());
        assertTrue(ChatMessage.printMessages().contains(msg2.getId()), "printMessages should show details of the second (last) message.");
    }

    // --- Storage Test (Basic) ---
    @Test
    void testStoreMessage_Draft() {
        ChatMessage draft = new ChatMessage(SENDER_PHONE, RECIPIENT_PHONE, "This is a draft.");
        // For a draft, index is 0. Filename should reflect this.
        // We're mostly checking it runs and returns the success string. File content checked elsewhere or manually.
        assertEquals("Message successfully stored.", draft.storeMessage());
        // To be thorough, you'd check if "message_stored_<ID>.json" was created.
    }

    @Test
    void testStoreMessage_SentMessage() {
        ChatMessage sentMsg = new ChatMessage(SENDER_PHONE, RECIPIENT_PHONE, "This was sent.");
        sentMsg.sentMessage(); // Index will be > 0
        assertEquals("Message successfully stored.", sentMsg.storeMessage());
        // To be thorough, you'd check if "message_<INDEX>.json" was created.
    }
    
    // --- printMessages with no messages sent ---
    @Test
    void testPrintMessages_NoMessagesSent() {
        // Relies on @BeforeEach to reset static counter
        assertEquals("No messages sent", ChatMessage.printMessages());
    }
}
