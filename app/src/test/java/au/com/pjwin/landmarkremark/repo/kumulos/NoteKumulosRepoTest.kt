package au.com.pjwin.landmarkremark.repo.kumulos

import au.com.pjwin.landmarkremark.BaseTest
import com.google.android.gms.maps.model.LatLng
import junit.framework.Assert.assertNull
import org.junit.Test
import org.mockito.Spy
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class NoteKumulosRepoTest : BaseTest() {

    @Spy
    private lateinit var noteRepo: NoteKumulosRepo

    @Test
    fun testAddNote() {
        val latch = CountDownLatch(1)
        noteRepo.saveNote("testAddNote", "-33.853", "151.211",
                {
                    assert(it > 0)
                    latch.countDown()
                },
                {
                    assertNull(it)
                    latch.countDown()
                })

        latch.await(5, TimeUnit.SECONDS)
    }

    @Test
    fun testGetAllNotesByUser() {
        val latch = CountDownLatch(1)
        noteRepo.getNotesByUser("1",
                {
                    print(it.size)
                    assert(it.size >= 0)
                    latch.countDown()
                },
                {
                    print(it.message)
                    assertNull(it)
                    latch.countDown()
                })

        latch.await(5, TimeUnit.SECONDS)
    }
}