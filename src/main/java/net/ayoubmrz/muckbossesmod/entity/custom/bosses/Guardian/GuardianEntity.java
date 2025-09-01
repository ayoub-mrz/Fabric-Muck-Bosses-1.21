package net.ayoubmrz.muckbossesmod.entity.custom.bosses.Guardian;

public interface GuardianEntity {
    void setShootingLightning(boolean shooting);
    void setShootingLazer(boolean shooting);
    void setLazerSoundStart(boolean start);

    float getLaser();
    float getLightning();
}
