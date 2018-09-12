package au.com.pjwin.landmarkremark

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import au.com.pjwin.landmarkremark.model.Note
import au.com.pjwin.landmarkremark.model.User
import au.com.pjwin.landmarkremark.viewmodel.AuthViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import junit.framework.Assert.assertNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class LoginTest {

    private lateinit var auth: FirebaseAuth
    private lateinit var latch: CountDownLatch

    @Captor
    private lateinit var userCaptor: ArgumentCaptor<User>

    @Spy
    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        FirebaseApp.initializeApp(InstrumentationRegistry.getTargetContext())
        auth = FirebaseAuth.getInstance()
    }

    @Test
    fun testLoginAnonymously() {
        latch = CountDownLatch(1)

        authViewModel.loginAnonymous()

        Thread.sleep(5000)
        Mockito.verify(authViewModel).onData(userCaptor.capture())
    }

    //@Test
    fun testCreateNote() {
        val dbRef = FirebaseDatabase.getInstance().getReference("notes")
        val id = dbRef.push().key
        //assertNotNull(id)

        latch = CountDownLatch(1)
        //DataSnapshot
        id?.let {
            dbRef.setValue(Note(it, "mark 1", "", "-33.852", "151.211"))
                    .addOnCompleteListener { task ->
                        latch.countDown()
                        //assertTrue(task.isSuccessful)
                        if (task.isSuccessful) {
                            //assertNotNull(task.result)

                        }
                        assertNull(task.exception)
                    }
        }

        latch.await(5, TimeUnit.SECONDS)
    }

    @After
    fun finish() {
        auth.signOut()
    }
}
