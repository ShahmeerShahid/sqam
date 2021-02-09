CREATE TRIGGER insert_referral_trigger 
     BEFORE INSERT OR UPDATE ON referral 
   FOR EACH ROW EXECUTE PROCEDURE insert_referral_check();