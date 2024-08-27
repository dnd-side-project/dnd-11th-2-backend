-- insert challenge data
INSERT INTO challenge(id, name, expected_time, challenge_type, image_url)
VALUES
    (1, '어제보다 1km더 뛰기', 480, 'DEFEAT_YESTERDAY', 'https://d27big3ufowabi.cloudfront.net/challenge_distance.png'),
    (2, '어제보다 5분 더 뛰기', 300, 'DEFEAT_YESTERDAY', 'https://d27big3ufowabi.cloudfront.net/challenge_time.png'),
    (3, '어제보다 평균 페이스 10초 빠르게 뛰기', 0, 'DEFEAT_YESTERDAY', 'https://d27big3ufowabi.cloudfront.net/challenge_pace.png'),
    (4, '오늘 5km 뛰기', 2400, 'TODAY', 'https://d27big3ufowabi.cloudfront.net/challenge_distance.png'),
    (5, '오늘 30분 동안 뛰기', 1800, 'TODAY', 'https://d27big3ufowabi.cloudfront.net/challenge_time.png'),
    (6, '1km 6분안에 뛰기', 360, 'DISTANCE_IN_TIME', 'https://d27big3ufowabi.cloudfront.net/challenge_pace.png');


-- insert challenge_goal_condition data
INSERT INTO challenge_goal_condition(challenge_id, goal_type, goal_value, comparison_type)
VALUES
    (1, 'DISTANCE', 1000, 'GREATER_THAN_OR_EQUAL_TO'),
    (2, 'TIME', 300, 'GREATER_THAN_OR_EQUAL_TO'),
    (3, 'PACE', 10, 'LESS_THAN_OR_EQUAL_TO'),
    (4, 'DISTANCE', 5000, 'GREATER_THAN_OR_EQUAL_TO'),
    (5, 'TIME', 1800, 'GREATER_THAN_OR_EQUAL_TO'),
    (6, 'DISTANCE', 1000, 'GREATER_THAN_OR_EQUAL_TO'),
    (6, 'PACE', 360, 'LESS_THAN_OR_EQUAL_TO');
