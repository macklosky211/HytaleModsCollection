package com.katsi.dailyloginreward;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginStore {
    ArrayList<DailyReward> login_rewards = new ArrayList<DailyReward>();
    Map<String, LoginObject> map;

    private static LoginStore instance;

    public LoginStore() {
        instance = this;
        map = new HashMap<String, LoginObject>();
    }

    public static void setInstance(LoginStore new_instance) {
        instance = new_instance;
        if (new_instance.map == null) new_instance.map = new HashMap<String, LoginObject>();
        if (new_instance.login_rewards == null) new_instance.login_rewards = new ArrayList<DailyReward>();
    }

    public static LoginStore get() {
        return instance;
    }

    public String toString() {
        return String.format("LoginStore : {\n\tmap : %s,\n\tlogin_rewards : %s\n}", this.map, this.login_rewards);
    }

    public static void setLoginRewards(ArrayList<DailyReward> login_rewards) {
        instance.login_rewards = login_rewards;
    }

    public static boolean hasRewardAvailable(UUID uuid) {
        String uuid_string = uuid.toString();

        if (!instance.map.containsKey(uuid_string)) {
            return true; 
        } 

        LoginObject last_login = instance.map.get(uuid_string);

        int days_since_last_login = last_login.getDaysSinceLastLogin();

        return days_since_last_login >= 1;
    }

    public static DailyReward getDailyReward(UUID uuid){
        String uuid_string = uuid.toString();

        LoginObject user_login;
        if (instance.map.containsKey(uuid_string)) {
            user_login = instance.map.get(uuid_string);
            int num_days_since_last_login = user_login.getDaysSinceLastLogin();
            if (num_days_since_last_login <= 0) { // This means the player already got their item today.
                return null;
            }
        } else {
            user_login = new LoginObject(LocalDate.now().minusDays(2).toEpochDay());
            instance.map.put(uuid_string, user_login);
        }
        
        instance.calculateLoginStreak(user_login);
        
        return getDailyRewardForStreak(user_login.getLoginStreak());
    }

    public static DailyReward getDailyRewardForStreak(Double streak_day) {
        streak_day = Math.min(Math.abs(streak_day), instance.login_rewards.size() - 1);
        DailyReward daily_reward = instance.login_rewards.get(streak_day.intValue());
        return daily_reward;
    }

    private int calculateLoginStreak(LoginObject login) {
        int num_days_since_last_login = login.getDaysSinceLastLogin();

        login.setLastLogin(LocalDate.now().toEpochDay());

        if(num_days_since_last_login == 1){
            login.setLoginStreak(login.getLoginStreak() + 1d);
        //give normal rewards
        } else if (num_days_since_last_login > 1d) {
            //reset and give day 1 reward
            login.setLoginStreak(0d);
        }
        else {
            //user already claimed their reward this day.
            System.err.println("[DailyLoginReward] Login streak was <= 0, this shouldnt be possible");
        }

        return login.getLoginStreak().intValue();
    }
    
    static class LoginObject {
        private long last_login_epoch;
        private Double login_streak;
    
        public LoginObject(long last_login_epoch) {
            this(last_login_epoch, 0d);
        }
    
        public LoginObject(long last_login_epoch, Double login_streak) {
            this.last_login_epoch = last_login_epoch;
            this.login_streak = login_streak;
        }
    
        public long getLastLoginEpoch() {
            return last_login_epoch;
        }
        
        public void setLastLogin(long last_login_epoch) {
            this.last_login_epoch = last_login_epoch;
        }
        
        public Double getLoginStreak() {
            return login_streak;
        }
    
        public void setLoginStreak(Double login_streak) {
            this.login_streak = login_streak;
        }
    
        public int getDaysSinceLastLogin() {
            return Math.abs(LocalDate.ofEpochDay(this.last_login_epoch).until(LocalDate.now()).getDays());
        }
    
        public String toString() {
            return String.format("LoginObject : {last_login_epoch: %d, login_streak: %f}", this.last_login_epoch, this.login_streak);
        }
    
        
    }
}
