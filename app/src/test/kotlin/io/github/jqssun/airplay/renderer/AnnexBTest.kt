package io.github.jqssun.airplay.renderer
import org.junit.Assert.*
import org.junit.Test
class AnnexBTest {
    @Test fun parameterSetsAfterSeiAreRecognized() {
        assertTrue(AnnexB.isKeyframe(byteArrayOf(0,0,0,1,6,50,0,0,0,1,7,20), false))
        assertTrue(AnnexB.isKeyframe(byteArrayOf(0,0,1,9,50,0,0,1,5,20), false))
        assertFalse(AnnexB.isKeyframe(byteArrayOf(0,0,0,1,1,20), false))
        assertFalse(AnnexB.isKeyframe(byteArrayOf(0,0,1), false))
        assertTrue(AnnexB.isKeyframe(byteArrayOf(0,0,0,1,64,1), true))
    }
}
