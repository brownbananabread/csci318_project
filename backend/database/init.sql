CREATE TYPE user_role AS ENUM ('customer', 'business', 'admin');
CREATE TYPE job_status AS ENUM ('unassigned', 'assigned', 'pending_payment', 'complete');

CREATE TABLE users (
    userid SERIAL PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    service VARCHAR(255),
    ratings INTEGER[] DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_service_business_only 
        CHECK ((role = 'business' AND service IS NOT NULL) OR 
               (role IN ('customer', 'admin') AND service IS NULL))
);

CREATE TABLE jobs (
    jobid SERIAL PRIMARY KEY,
    customerid INT NOT NULL,
    businessid INT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    service VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status job_status NOT NULL DEFAULT 'unassigned',
    location VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_customer 
        FOREIGN KEY (customerid) REFERENCES users(userid)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    
    CONSTRAINT fk_business 
        FOREIGN KEY (businessid) REFERENCES users(userid)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_jobs_updated_at BEFORE UPDATE ON jobs
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TABLE activity (
    activityid SERIAL PRIMARY KEY,
    userid INT NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_activity_user 
        FOREIGN KEY (userid) REFERENCES users(userid)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE INDEX idx_activity_userid ON activity(userid);

CREATE INDEX idx_activity_created_at ON activity(created_at);

INSERT INTO users (firstname, lastname, email, password, role, service, ratings) VALUES
-- Customers
('Emma', 'Johnson', 'emma.johnson@email.com', 'password123', 'customer', NULL, '{}'),
('Oliver', 'Williams', 'oliver.williams@email.com', 'password123', 'customer', NULL, '{}'),
('Sophia', 'Brown', 'sophia.brown@email.com', 'password123', 'customer', NULL, '{}'),
('Liam', 'Jones', 'liam.jones@email.com', 'password123', 'customer', NULL, '{}'),
('Ava', 'Garcia', 'ava.garcia@email.com', 'password123', 'customer', NULL, '{}'),
('Noah', 'Miller', 'noah.miller@email.com', 'password123', 'customer', NULL, '{}'),
('Isabella', 'Davis', 'isabella.davis@email.com', 'password123', 'customer', NULL, '{}'),
('Mason', 'Rodriguez', 'mason.rodriguez@email.com', 'password123', 'customer', NULL, '{}'),
('Mia', 'Martinez', 'mia.martinez@email.com', 'password123', 'customer', NULL, '{}'),
('William', 'Hernandez', 'william.hernandez@email.com', 'password123', 'customer', NULL, '{}'),
('Charlotte', 'Lopez', 'charlotte.lopez@email.com', 'password123', 'customer', NULL, '{}'),
('James', 'Gonzalez', 'james.gonzalez@email.com', 'password123', 'customer', NULL, '{}'),
('Amelia', 'Wilson', 'amelia.wilson@email.com', 'password123', 'customer', NULL, '{}'),
('Benjamin', 'Anderson', 'benjamin.anderson@email.com', 'password123', 'customer', NULL, '{}'),
('Harper', 'Thomas', 'harper.thomas@email.com', 'password123', 'customer', NULL, '{}'),
('Lucas', 'Taylor', 'lucas.taylor@email.com', 'password123', 'customer', NULL, '{}'),
('Evelyn', 'Moore', 'evelyn.moore@email.com', 'password123', 'customer', NULL, '{}'),
('Alexander', 'Jackson', 'alexander.jackson@email.com', 'password123', 'customer', NULL, '{}'),
('Abigail', 'Martin', 'abigail.martin@email.com', 'password123', 'customer', NULL, '{}'),
('Ethan', 'Lee', 'ethan.lee@email.com', 'password123', 'customer', NULL, '{}'),
('Elizabeth', 'Perez', 'elizabeth.perez@email.com', 'password123', 'customer', NULL, '{}'),
('Jacob', 'Thompson', 'jacob.thompson@email.com', 'password123', 'customer', NULL, '{}'),
('Emily', 'White', 'emily.white@email.com', 'password123', 'customer', NULL, '{}'),
('Daniel', 'Harris', 'daniel.harris@email.com', 'password123', 'customer', NULL, '{}'),
('Madison', 'Sanchez', 'madison.sanchez@email.com', 'password123', 'customer', NULL, '{}'),
('Aiden', 'Clark', 'aiden.clark@email.com', 'password123', 'customer', NULL, '{}'),
('Ella', 'Ramirez', 'ella.ramirez@email.com', 'password123', 'customer', NULL, '{}'),
('Matthew', 'Lewis', 'matthew.lewis@email.com', 'password123', 'customer', NULL, '{}'),
('Avery', 'Robinson', 'avery.robinson@email.com', 'password123', 'customer', NULL, '{}'),
('Joseph', 'Walker', 'joseph.walker@email.com', 'password123', 'customer', NULL, '{}'),
('Sofia', 'Young', 'sofia.young@email.com', 'password123', 'customer', NULL, '{}'),
('David', 'Allen', 'david.allen@email.com', 'password123', 'customer', NULL, '{}'),
('Scarlett', 'King', 'scarlett.king@email.com', 'password123', 'customer', NULL, '{}'),
('Carter', 'Wright', 'carter.wright@email.com', 'password123', 'customer', NULL, '{}'),
('Grace', 'Scott', 'grace.scott@email.com', 'password123', 'customer', NULL, '{}'),
('Jayden', 'Torres', 'jayden.torres@email.com', 'password123', 'customer', NULL, '{}'),
('Chloe', 'Nguyen', 'chloe.nguyen@email.com', 'password123', 'customer', NULL, '{}'),
('Owen', 'Hill', 'owen.hill@email.com', 'password123', 'customer', NULL, '{}'),
('Riley', 'Flores', 'riley.flores@email.com', 'password123', 'customer', NULL, '{}'),
('Andrew', 'Green', 'andrew.green@email.com', 'password123', 'customer', NULL, '{}'),

-- Business
('Robert', 'Adams', 'robert.adams@email.com', 'password123', 'business', 'Plumbing', '{5,4,5,5,3}'),
('Jennifer', 'Nelson', 'jennifer.nelson@email.com', 'password123', 'business', 'Electrical', '{4,5,4,5,5}'),
('Thomas', 'Baker', 'thomas.baker@email.com', 'password123', 'business', 'Carpentry', '{5,5,4,5,4}'),
('Jessica', 'Hall', 'jessica.hall@email.com', 'password123', 'business', 'House Cleaning', '{4,4,5,4,5}'),
('Christopher', 'Rivera', 'christopher.rivera@email.com', 'password123', 'business', 'Landscaping', '{5,4,5,5,5}'),
('Sarah', 'Campbell', 'sarah.campbell@email.com', 'password123', 'business', 'Painting', '{4,5,4,4,5}'),
('Kevin', 'Mitchell', 'kevin.mitchell@email.com', 'password123', 'business', 'HVAC', '{5,5,5,4,5}'),
('Lisa', 'Carter', 'lisa.carter@email.com', 'password123', 'business', 'Moving Services', '{4,4,5,5,4}'),
('Brian', 'Roberts', 'brian.roberts@email.com', 'password123', 'business', 'Roofing', '{5,4,5,4,5}'),
('Amy', 'Gomez', 'amy.gomez@email.com', 'password123', 'business', 'Plumbing', '{4,5,4,5,4}'),
('Jason', 'Phillips', 'jason.phillips@email.com', 'password123', 'business', 'Electrical', '{5,5,4,5,5}'),
('Michelle', 'Evans', 'michelle.evans@email.com', 'password123', 'business', 'Carpentry', '{4,4,5,4,4}'),
('Ryan', 'Turner', 'ryan.turner@email.com', 'password123', 'business', 'House Cleaning', '{5,4,5,5,4}'),
('Laura', 'Diaz', 'laura.diaz@email.com', 'password123', 'business', 'Landscaping', '{4,5,4,4,5}'),
('Eric', 'Parker', 'eric.parker@email.com', 'password123', 'business', 'Painting', '{5,5,5,5,4}'),
('Amanda', 'Cruz', 'amanda.cruz@email.com', 'password123', 'business', 'HVAC', '{4,4,4,5,5}'),
('Steven', 'Edwards', 'steven.edwards@email.com', 'password123', 'business', 'Moving Services', '{5,4,5,4,4}'),
('Kimberly', 'Collins', 'kimberly.collins@email.com', 'password123', 'business', 'Roofing', '{4,5,4,5,5}'),
('Paul', 'Reyes', 'paul.reyes@email.com', 'password123', 'business', 'Plumbing', '{5,5,5,4,5}'),
('Donna', 'Stewart', 'donna.stewart@email.com', 'password123', 'business', 'Electrical', '{4,4,5,5,4}'),
('Gary', 'Morris', 'gary.morris@email.com', 'password123', 'business', 'Carpentry', '{5,4,4,5,5}'),
('Carol', 'Morales', 'carol.morales@email.com', 'password123', 'business', 'House Cleaning', '{4,5,5,4,4}'),
('Edward', 'Murphy', 'edward.murphy@email.com', 'password123', 'business', 'Landscaping', '{5,5,4,5,4}'),
('Sandra', 'Cook', 'sandra.cook@email.com', 'password123', 'business', 'Painting', '{4,4,5,4,5}'),
('Gregory', 'Rogers', 'gregory.rogers@email.com', 'password123', 'business', 'HVAC', '{5,4,5,5,5}'),
('Janet', 'Morgan', 'janet.morgan@email.com', 'password123', 'business', 'Moving Services', '{4,5,4,4,4}'),
('Raymond', 'Peterson', 'raymond.peterson@email.com', 'password123', 'business', 'Roofing', '{5,5,5,5,4}'),
('Catherine', 'Cooper', 'catherine.cooper@email.com', 'password123', 'business', 'Plumbing', '{4,4,4,5,5}'),
('Dennis', 'Reed', 'dennis.reed@email.com', 'password123', 'business', 'Electrical', '{5,4,5,4,4}'),
('Ruth', 'Bailey', 'ruth.bailey@email.com', 'password123', 'business', 'Carpentry', '{4,5,4,5,5}'),
('Jerry', 'Bell', 'jerry.bell@email.com', 'password123', 'business', 'House Cleaning', '{5,5,5,4,5}'),
('Joyce', 'Gonzales', 'joyce.gonzales@email.com', 'password123', 'business', 'Landscaping', '{4,4,5,5,4}'),
('Albert', 'Washington', 'albert.washington@email.com', 'password123', 'business', 'Painting', '{5,4,4,5,4}'),
('Virginia', 'Butler', 'virginia.butler@email.com', 'password123', 'business', 'HVAC', '{4,5,5,4,5}'),
('Harold', 'Simmons', 'harold.simmons@email.com', 'password123', 'business', 'Moving Services', '{5,5,4,5,5}'),
('Helen', 'Foster', 'helen.foster@email.com', 'password123', 'business', 'Roofing', '{4,4,5,4,4}'),
('Carl', 'Gonzales', 'carl.gonzales@email.com', 'password123', 'business', 'Plumbing', '{5,4,5,5,4}'),
('Frances', 'Bryant', 'frances.bryant@email.com', 'password123', 'business', 'Electrical', '{4,5,4,4,5}'),
('Arthur', 'Alexander', 'arthur.alexander@email.com', 'password123', 'business', 'Carpentry', '{5,5,5,5,5}'),
('Judith', 'Russell', 'judith.russell@email.com', 'password123', 'business', 'House Cleaning', '{4,4,4,5,4}'),
('Willie', 'Griffin', 'willie.griffin@email.com', 'password123', 'business', 'Landscaping', '{5,4,5,4,5}'),
('Joan', 'Diaz', 'joan.diaz@email.com', 'password123', 'business', 'Painting', '{4,5,4,5,4}'),
('Ralph', 'Hayes', 'ralph.hayes@email.com', 'password123', 'business', 'HVAC', '{5,5,5,4,4}'),
('Marie', 'Myers', 'marie.myers@email.com', 'password123', 'business', 'Moving Services', '{4,4,5,5,5}'),
('Eugene', 'Ford', 'eugene.ford@email.com', 'password123', 'business', 'Roofing', '{5,4,4,5,5}'),
('Mildred', 'Hamilton', 'mildred.hamilton@email.com', 'password123', 'business', 'Plumbing', '{4,5,5,4,4}'),
('Russell', 'Graham', 'russell.graham@email.com', 'password123', 'business', 'Electrical', '{5,5,4,5,4}'),
('Rose', 'Sullivan', 'rose.sullivan@email.com', 'password123', 'business', 'Carpentry', '{4,4,5,4,5}'),
('Louis', 'Wallace', 'louis.wallace@email.com', 'password123', 'business', 'House Cleaning', '{5,4,5,5,5}'),

-- Admin
('SuperAdmin', 'One', 'superadmin1@email.com', 'password123', 'admin', NULL, '{}'),
('AdminTwo', 'Support', 'admin2@email.com', 'password123', 'admin', NULL, '{}'),
('AdminThree', 'Manager', 'admin3@email.com', 'password123', 'admin', NULL, '{}'),
('AdminFour', 'Supervisor', 'admin4@email.com', 'password123', 'admin', NULL, '{}'),
('AdminFive', 'Moderator', 'admin5@email.com', 'password123', 'admin', NULL, '{}'),
('AdminSix', 'Coordinator', 'admin6@email.com', 'password123', 'admin', NULL, '{}'),
('AdminSeven', 'Director', 'admin7@email.com', 'password123', 'admin', NULL, '{}'),
('AdminEight', 'Executive', 'admin8@email.com', 'password123', 'admin', NULL, '{}'),
('AdminNine', 'Chief', 'admin9@email.com', 'password123', 'admin', NULL, '{}'),
('AdminTen', 'Lead', 'admin10@email.com', 'password123', 'admin', NULL, '{}');

INSERT INTO jobs (customerid, businessid, title, description, service, amount, status, location) VALUES

(1, 41, 'Fix Leaking Faucet', 'Master bathroom faucet dripping constantly', 'Plumbing', 125.00, 'complete', '123 Oak St, Apt 5A'),
(2, 42, 'Install New Outlets', 'Need 4 new outlets in garage workspace', 'Electrical', 450.00, 'assigned', '456 Pine Ave'),
(3, 43, 'Build Deck Stairs', 'Replace old deck stairs with new ones', 'Carpentry', 850.00, 'pending_payment', '789 Maple Dr'),
(4, 44, 'Deep Clean After Party', 'Post-event cleaning for large home', 'House Cleaning', 300.00, 'complete', '321 Elm Ct'),
(5, 45, 'Spring Lawn Care', 'Fertilize, aerate, and seed lawn', 'Landscaping', 275.00, 'assigned', '654 Birch Ln'),
(6, 46, 'Paint Master Bedroom', 'Two coats, ceiling and trim included', 'Painting', 650.00, 'pending_payment', '987 Cedar Rd'),
(7, 47, 'AC Unit Service', 'Annual maintenance and filter replacement', 'HVAC', 150.00, 'complete', '111 Willow Way'),
(8, 48, 'Office Relocation', 'Move office furniture and equipment', 'Moving Services', 1200.00, 'assigned', '222 Business Park'),
(9, 49, 'Roof Leak Repair', 'Fix leak around chimney area', 'Roofing', 450.00, 'complete', '333 Highland Ave'),
(10, 50, 'Toilet Installation', 'Replace old toilet with new model', 'Plumbing', 350.00, 'pending_payment', '444 Valley View'),

-- Unassigned jobs
(11, NULL, 'Emergency Pipe Burst', 'Basement pipe needs immediate repair', 'Plumbing', 500.00, 'unassigned', '555 River Rd'),
(12, NULL, 'Rewire Garage', 'Complete electrical rewiring needed', 'Electrical', 2200.00, 'unassigned', '666 Mountain Dr'),
(13, NULL, 'Custom Shelving', 'Built-in shelves for home library', 'Carpentry', 1500.00, 'unassigned', '777 Forest Ave'),
(14, NULL, 'Move-out Cleaning', 'Complete apartment cleaning for move-out', 'House Cleaning', 250.00, 'unassigned', '888 Park Pl'),
(15, NULL, 'Tree Removal', 'Remove large dead oak tree', 'Landscaping', 800.00, 'unassigned', '999 Grove St'),
(16, NULL, 'Exterior House Paint', 'Full exterior paint job needed', 'Painting', 3500.00, 'unassigned', '1010 Hill Ct'),
(17, NULL, 'Furnace Replacement', 'Old furnace needs replacement', 'HVAC', 3000.00, 'unassigned', '1111 Winter Ln'),
(18, NULL, 'Estate Move', 'Full estate moving services needed', 'Moving Services', 2500.00, 'unassigned', '1212 Manor Dr'),
(19, NULL, 'New Roof Installation', 'Complete roof replacement required', 'Roofing', 8000.00, 'unassigned', '1313 Summit Rd'),
(20, NULL, 'Bathroom Remodel Plumbing', 'All new plumbing for bathroom remodel', 'Plumbing', 1800.00, 'unassigned', '1414 Creek Ct'),

-- Mix of assigned jobs
(21, 51, 'Kitchen Sink Repair', 'Garbage disposal not working', 'Plumbing', 175.00, 'assigned', '1515 Lake Ave'),
(22, 52, 'Security Light Install', 'Motion sensor lights for driveway', 'Electrical', 325.00, 'complete', '1616 Shore Dr'),
(23, 53, 'Fence Gate Repair', 'Gate hinges broken, needs replacement', 'Carpentry', 225.00, 'assigned', '1717 Beach Rd'),
(24, 54, 'Weekly Cleaning Service', 'Regular weekly house cleaning', 'House Cleaning', 120.00, 'pending_payment', '1818 Coast Hwy'),
(25, 55, 'Hedge Trimming', 'Trim overgrown hedges around property', 'Landscaping', 150.00, 'complete', '1919 Garden Way'),
(26, 56, 'Garage Door Painting', 'Paint and seal garage doors', 'Painting', 400.00, 'assigned', '2020 Parkway Dr'),
(27, 57, 'Heat Pump Repair', 'Heat pump not heating properly', 'HVAC', 375.00, 'complete', '2121 Warm Springs'),
(28, 58, 'Apartment Move', 'Studio apartment local move', 'Moving Services', 450.00, 'pending_payment', '2222 Urban Pl'),
(29, 59, 'Gutter Installation', 'Install new gutters on garage', 'Roofing', 650.00, 'assigned', '2323 Rain St'),
(30, 60, 'Water Heater Install', 'Replace old water heater', 'Plumbing', 1200.00, 'complete', '2424 Hot Springs'),

-- Continue with more varied jobs
(31, 61, 'Outdoor Outlet Install', 'Add weatherproof outlets to patio', 'Electrical', 275.00, 'pending_payment', '2525 Patio Ln'),
(32, 62, 'Bookshelf Assembly', 'Assemble and mount floating shelves', 'Carpentry', 150.00, 'assigned', '2626 Library St'),
(33, 63, 'Post-Construction Clean', 'Clean after kitchen renovation', 'House Cleaning', 450.00, 'complete', '2727 Reno Ave'),
(34, 64, 'Sod Installation', 'Install new sod in backyard', 'Landscaping', 1800.00, 'assigned', '2828 Green Acres'),
(35, 65, 'Cabinet Painting', 'Paint kitchen cabinets white', 'Painting', 1400.00, 'pending_payment', '2929 Kitchen Rd'),
(36, 66, 'Duct Cleaning', 'Clean all HVAC ducts in home', 'HVAC', 350.00, 'complete', '3030 Air Way'),
(37, 67, 'Piano Moving', 'Move grand piano to new home', 'Moving Services', 600.00, 'assigned', '3131 Music Ave'),
(38, 68, 'Skylight Repair', 'Fix leaking skylight seal', 'Roofing', 425.00, 'complete', '3232 Sky View'),
(39, 69, 'Drain Cleaning', 'Clear all slow drains in house', 'Plumbing', 225.00, 'pending_payment', '3333 Flow St'),
(40, 70, 'Panel Upgrade', 'Upgrade electrical panel to 200 amp', 'Electrical', 2800.00, 'assigned', '3434 Power Dr'),

-- More jobs to reach 100
(1, 71, 'Deck Building', 'Build new 12x16 deck', 'Carpentry', 3500.00, 'complete', '3535 Deck Ln'),
(2, 72, 'Move-in Cleaning', 'Deep clean before moving in', 'House Cleaning', 350.00, 'assigned', '3636 Fresh Start'),
(3, 73, 'Sprinkler System', 'Install automatic sprinkler system', 'Landscaping', 2500.00, 'pending_payment', '3737 Water Way'),
(4, 74, 'Bathroom Painting', 'Paint and waterproof bathroom', 'Painting', 450.00, 'complete', '3838 Bath Ave'),
(5, 75, 'Mini Split Install', 'Install ductless mini split', 'HVAC', 3200.00, 'assigned', '3939 Cool Breeze'),
(6, 76, 'Storage Unit Move', 'Move items to storage unit', 'Moving Services', 300.00, 'complete', '4040 Store Rd'),
(7, 77, 'Chimney Repair', 'Repair chimney cap and flashing', 'Roofing', 575.00, 'pending_payment', '4141 Chimney Ln'),
(8, 78, 'Pipe Replacement', 'Replace main water line', 'Plumbing', 2400.00, 'assigned', '4242 Main Line'),
(9, 79, 'Smart Home Wiring', 'Wire for smart home devices', 'Electrical', 850.00, 'complete', '4343 Smart Ave'),
(10, 80, 'Crown Molding', 'Install crown molding in living room', 'Carpentry', 650.00, 'assigned', '4444 Crown Ct'),

-- Final batch to reach 100
(11, 81, 'Spring Cleaning', 'Comprehensive spring cleaning', 'House Cleaning', 400.00, 'pending_payment', '4545 Spring Rd'),
(12, 82, 'Garden Design', 'Design and plant flower garden', 'Landscaping', 1200.00, 'complete', '4646 Bloom St'),
(13, 83, 'Stairwell Painting', 'Paint interior stairwell', 'Painting', 550.00, 'assigned', '4747 Step Up'),
(14, 84, 'Boiler Service', 'Annual boiler maintenance', 'HVAC', 225.00, 'complete', '4848 Heat Ln'),
(15, 85, 'Garage Cleanout', 'Help move items from garage', 'Moving Services', 250.00, 'pending_payment', '4949 Clear Out'),
(16, 86, 'Flashing Repair', 'Repair roof flashing around vents', 'Roofing', 375.00, 'assigned', '5050 Seal Dr'),
(17, 87, 'Sump Pump Install', 'Install new sump pump system', 'Plumbing', 950.00, 'complete', '5151 Dry Base'),
(18, 88, 'EV Charger Install', 'Install level 2 EV charger', 'Electrical', 1500.00, 'assigned', '5252 Charge St'),
(19, 89, 'Closet Organization', 'Build custom closet organizers', 'Carpentry', 850.00, 'pending_payment', '5353 Organize Ln'),
(20, 90, 'Office Cleaning', 'Weekly office cleaning service', 'House Cleaning', 150.00, 'complete', '5454 Work Pl'),

-- Unassigned jobs to fill out
(21, NULL, 'Lawn Renovation', 'Complete lawn renovation needed', 'Landscaping', 2200.00, 'unassigned', '5555 Grass Ct'),
(22, NULL, 'House Numbers Paint', 'Paint house numbers and mailbox', 'Painting', 125.00, 'unassigned', '5656 Number Way'),
(23, NULL, 'Thermostat Upgrade', 'Install smart thermostat', 'HVAC', 350.00, 'unassigned', '5757 Smart Heat'),
(24, NULL, 'Downsizing Move', 'Help with downsizing and moving', 'Moving Services', 1800.00, 'unassigned', '5858 Small Pl'),
(25, NULL, 'Solar Panel Cleaning', 'Clean solar panels on roof', 'Roofing', 200.00, 'unassigned', '5959 Solar Dr'),
(26, NULL, 'Septic Inspection', 'Annual septic system inspection', 'Plumbing', 300.00, 'unassigned', '6060 Septic Ln'),
(27, NULL, 'Surge Protection', 'Install whole house surge protector', 'Electrical', 650.00, 'unassigned', '6161 Protect Ave'),
(28, NULL, 'Pergola Build', 'Build pergola over patio', 'Carpentry', 2800.00, 'unassigned', '6262 Shade St'),
(29, NULL, 'Vacation Cleaning', 'Clean house while on vacation', 'House Cleaning', 200.00, 'unassigned', '6363 Away Rd'),
(30, NULL, 'Mulch Delivery', 'Deliver and spread mulch', 'Landscaping', 350.00, 'unassigned', '6464 Mulch Way'),

-- Final 10 jobs
(31, 41, 'Caulk Windows', 'Re-caulk all exterior windows', 'Painting', 275.00, 'complete', '6565 Seal Ct'),
(32, 47, 'Filter Change', 'Change all HVAC filters', 'HVAC', 75.00, 'assigned', '6666 Clean Air'),
(33, 48, 'Safe Moving', 'Move heavy safe to basement', 'Moving Services', 400.00, 'pending_payment', '6767 Heavy Ln'),
(34, 49, 'Vent Cleaning', 'Clean all roof vents', 'Roofing', 250.00, 'complete', '6868 Vent Dr'),
(35, 50, 'Shower Repair', 'Fix leaking shower valve', 'Plumbing', 325.00, 'assigned', '6969 Shower St'),
(36, 51, 'Doorbell Install', 'Install video doorbell system', 'Electrical', 225.00, 'complete', '7070 Ring Ave'),
(37, 53, 'Shelf Repair', 'Repair sagging garage shelves', 'Carpentry', 175.00, 'pending_payment', '7171 Shelf Rd'),
(38, 54, 'Biweekly Cleaning', 'Regular biweekly service', 'House Cleaning', 100.00, 'assigned', '7272 Clean Ln'),
(39, 55, 'Leaf Cleanup', 'Fall leaf removal service', 'Landscaping', 225.00, 'complete', '7373 Leaf Way'),
(40, 56, 'Touch-up Paint', 'Touch up paint throughout house', 'Painting', 350.00, 'assigned', '7474 Touch Ct');

INSERT INTO activity (userid, description) VALUES
-- Customer activities
(1, 'Logged in to platform'),
(1, 'Created new plumbing job request'),
(1, 'Viewed job status update'),
(2, 'Updated profile information'),
(2, 'Assigned job to electrical contractor'),
(3, 'Requested quote for carpentry work'),
(3, 'Approved job completion'),
(4, 'Posted house cleaning job'),
(4, 'Left 5-star rating for service'),
(5, 'Browsed available landscapers'),
(5, 'Scheduled consultation'),
(6, 'Updated job requirements'),
(7, 'Canceled job request'),
(8, 'Made payment for completed job'),
(9, 'Contacted support about issue'),
(10, 'Changed password'),
(11, 'Uploaded job photos'),
(12, 'Requested emergency service'),
(13, 'Compared contractor quotes'),
(14, 'Extended job deadline'),
(15, 'Reported issue with contractor'),
(16, 'Saved contractor to favorites'),
(17, 'Shared job on social media'),
(18, 'Downloaded invoice'),
(19, 'Updated payment method'),
(20, 'Enabled notifications'),

-- Business activities
(41, 'Updated service area'),
(41, 'Accepted new job assignment'),
(42, 'Submitted quote for electrical work'),
(42, 'Marked job as complete'),
(43, 'Updated availability calendar'),
(43, 'Responded to customer message'),
(44, 'Added new service offering'),
(44, 'Uploaded certification documents'),
(45, 'Adjusted pricing structure'),
(45, 'Requested payment for completed job'),
(46, 'Updated business hours'),
(46, 'Added team member'),
(47, 'Completed safety training'),
(47, 'Updated insurance information'),
(48, 'Posted job update'),
(48, 'Uploaded before/after photos'),
(49, 'Claimed service area'),
(49, 'Responded to review'),
(50, 'Updated equipment list'),
(50, 'Scheduled follow-up visit'),
(51, 'Joined premium membership'),
(52, 'Completed background check'),
(53, 'Added portfolio images'),
(54, 'Set vacation mode'),
(55, 'Updated license information'),
(56, 'Requested early payment'),
(57, 'Completed customer survey'),
(58, 'Updated bank information'),
(59, 'Added emergency contact'),
(60, 'Verified phone number'),

-- More customer activities
(21, 'Searched for HVAC contractors'),
(22, 'Filtered search results by rating'),
(23, 'Viewed contractor profile'),
(24, 'Read contractor reviews'),
(25, 'Compared service prices'),
(26, 'Requested additional information'),
(27, 'Rescheduled appointment'),
(28, 'Added job to watchlist'),
(29, 'Invited contractor to bid'),
(30, 'Negotiated price with contractor'),
(31, 'Approved additional work'),
(32, 'Requested warranty information'),
(33, 'Filed complaint'),
(34, 'Withdrew job posting'),
(35, 'Updated job urgency'),
(36, 'Added tip for excellent service'),
(37, 'Referred friend to platform'),
(38, 'Joined customer rewards program'),
(39, 'Attended virtual consultation'),
(40, 'Requested itemized invoice'),

-- Admin activities
(90, 'Reviewed new user registration'),
(91, 'Approved business verification'),
(92, 'Resolved customer dispute'),
(93, 'Updated platform policies'),
(94, 'Banned user for violation'),
(95, 'Processed refund request'),
(96, 'Updated fee structure'),
(97, 'Sent platform announcement'),
(98, 'Generated monthly report'),
(99, 'Performed security audit'),

-- Final activities to reach 100
(41, 'Completed job successfully'),
(42, 'Received payment confirmation'),
(43, 'Updated tax information'),
(44, 'Downloaded tax documents'),
(45, 'Renewed business license'),
(46, 'Attended platform webinar'),
(47, 'Updated COVID-19 protocols'),
(48, 'Added new payment method'),
(49, 'Opted into SMS notifications'),
(50, 'Completed platform tutorial');