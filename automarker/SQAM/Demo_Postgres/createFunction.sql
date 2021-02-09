CREATE OR REPLACE FUNCTION insert_referral_check()
    RETURNS TRIGGER AS $insert_referral_trigger$
	BEGIN
	    IF NEW.custid = NEW.custref THEN
		   RAISE EXCEPTION 'A customer cannot refer self!';
		END IF;
		IF EXISTS(SELECT * FROM referral
		          WHERE custid = NEW.custref AND custref = NEW.custid) THEN
		   RAISE EXCEPTION 'No back reference allowed!';
		END IF;
		RETURN NEW;
	END;
$insert_referral_trigger$ LANGUAGE PLPGSQL;