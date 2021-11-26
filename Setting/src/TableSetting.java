import java.io.DataInput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;

import com.mysql.cj.protocol.Resultset;

public class TableSetting {
	public static void createDB() throws Exception {
		ResultSet rs = DBInterface.stmt.executeQuery("show databases like '2021지방_1'");
		if (rs.next()) {
			DBInterface.stmt.execute("drop database `2021지방_1`");
		}
		DBInterface.stmt.execute("create database `2021지방_1`");
		DBInterface.stmt.execute("use mysql");
		
		System.out.println("create DB:: Success");
	}
	
	public static void createGrants() throws Exception {
		ResultSet rs = DBInterface.stmt.executeQuery("select * from user where user='user'");
		if (rs.next()) {
			DBInterface.stmt.execute("drop user 'user'@'localhost'");
		}
		DBInterface.stmt.execute("create user 'user'@'localhost' identified by '1234'");
		DBInterface.stmt.execute("grant select, insert, delete, update on `2021지방_1`.* to 'user'@'localhost'");

		System.out.println("create Grants:: Success");
	}
	
	public static void createTables() throws Exception {
		DBInterface.stmt.execute("CREATE TABLE `2021지방_1`.`user` (  `u_no` INT NOT NULL AUTO_INCREMENT,  `u_id` VARCHAR(20) NULL,  `u_pw` VARCHAR(20) NULL,  `u_name` VARCHAR(15) NULL,  `u_phone` VARCHAR(20) NULL,  `u_age` DATE NULL,  `u_10percent` INT NULL,  `u_30percent` INT NULL,  PRIMARY KEY (`u_no`));");
		DBInterface.stmt.execute("CREATE TABLE `2021지방_1`.`category` (  `c_no` INT NOT NULL AUTO_INCREMENT,  `c_name` VARCHAR(10) NULL,  PRIMARY KEY (`c_no`));");
		DBInterface.stmt.execute("CREATE TABLE `2021지방_1`.`product` (  `p_no` INT NOT NULL AUTO_INCREMENT,  `c_no` INT NULL,  `p_name` VARCHAR(20) NULL,  `p_price` INT NULL,  `p_stock` INT NULL,  `p_explanation` VARCHAR(150) NULL,  PRIMARY KEY (`p_no`),  INDEX `product_fk_idx` (`c_no` ASC) VISIBLE,  CONSTRAINT `product_fk`    FOREIGN KEY (`c_no`)    REFERENCES `2021지방_1`.`category` (`c_no`)    ON DELETE NO ACTION    ON UPDATE NO ACTION);");
		DBInterface.stmt.execute("CREATE TABLE `2021지방_1`.`purchase` (  `pu_no` INT NOT NULL AUTO_INCREMENT,  `p_no` INT NULL,  `pu_price` INT NULL,  `pu_count` INT NULL,  `coupon` INT NULL,  `u_no` INT NULL,  `pu_date` DATE NULL,  PRIMARY KEY (`pu_no`),  INDEX `purchase_fk1_idx` (`p_no` ASC) VISIBLE,  INDEX `purchase_fk2_idx` (`u_no` ASC) VISIBLE,  CONSTRAINT `purchase_fk1`    FOREIGN KEY (`p_no`)    REFERENCES `2021지방_1`.`product` (`p_no`)    ON DELETE NO ACTION    ON UPDATE NO ACTION,  CONSTRAINT `purchase_fk2`    FOREIGN KEY (`u_no`)    REFERENCES `2021지방_1`.`user` (`u_no`)    ON DELETE NO ACTION    ON UPDATE NO ACTION);");
		DBInterface.stmt.execute("CREATE TABLE `2021지방_1`.`attendance` (  `a_no` INT NOT NULL AUTO_INCREMENT,  `u_no` INT NULL,  `a_date` DATE NULL,  PRIMARY KEY (`a_no`),  INDEX `attendance_fk_idx` (`u_no` ASC) VISIBLE,  CONSTRAINT `attendance_fk`    FOREIGN KEY (`u_no`)    REFERENCES `2021지방_1`.`user` (`u_no`)    ON DELETE NO ACTION    ON UPDATE NO ACTION);");
		DBInterface.stmt.execute("CREATE TABLE `2021지방_1`.`coupon` (  `c_no` INT NOT NULL AUTO_INCREMENT,  `u_no` INT NULL,  `c_date` VARCHAR(15) NULL,  `c_10percent` INT NULL,  `c_30percent` VARCHAR(45) NULL,  PRIMARY KEY (`c_no`),  INDEX `coupone_fk_idx` (`u_no` ASC) VISIBLE,  CONSTRAINT `coupone_fk`    FOREIGN KEY (`u_no`)    REFERENCES `2021지방_1`.`user` (`u_no`)    ON DELETE NO ACTION    ON UPDATE NO ACTION);");
		System.out.println("create Tables:: Success");
	}
	
	public static void createData() throws Exception {
		DBInterface.stmt.execute("use `2021지방_1`");
		
		dataInsert("user", "user");
		dataInsert("category", "category");
		dataInsert("product", "product");
		dataInsert("purchase", "purchase");
		dataInsert("attendance", "attendance");
		dataInsert("coupon", "coupon");
		
		System.out.println("create Data:: Success");
	}
	
	public static void dataInsert(String fileName, String tableName) throws Exception {
		String path = System.getProperty("user.dir") + "/datafile/" + fileName + ".txt";
		
		ResultSet rs = DBInterface.stmt.executeQuery("SHOW VARIABLES LIKE 'secure_file_priv'");
		rs.next();
		
		File in = new File(path);
		File out = new File(rs.getString(2) + fileName + ".txt");
		Files.copy(in.toPath(), out.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
		path = out.getPath().replace("\\", "/");
		DBInterface.stmt.execute("load data infile '" + path + "' into table " + tableName + " ignore 1 lines");
	}
}
