package woodward.joshua.ribbit.Model;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Joshua on 12/14/2014.
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Parse.initialize parameters include (Context, ApplicationID, ClientID).
        //Latter two generated by Parse.com
        Parse.initialize(this, "Yvm59SdwqKP0wcPAtRAmzGOepccJYOgGdKfijDui", "Dmhm8ygzJ2vLkodTokxC6HcAuRCJSCUf6dF8tsy0");
    }
}
