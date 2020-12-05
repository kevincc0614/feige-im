EXPLAIN SELECT
            t.unread_count,
            t.last_msg_id,
            t.conversation_id,
            t.target_id,
            t.conversation_name,
            t.conversation_avatar
        FROM
            (
                SELECT
                    COUNT( m.msg_id ) unread_count,
                    MAX( m.msg_id ) last_msg_id,
                    m.conversation_id conversation_id,
                    c.target_id target_id,
                    c.conversation_name conversation_name,
                    c.conversation_avatar conversation_avatar
                FROM
                    t_user_message m
                        RIGHT JOIN t_user_conversation c ON m.conversation_id = c.conversation_id
                        AND m.user_id = c.user_id
                WHERE
                        m.user_id = 406355803890966528
                  AND m.conversation_id = 406487153854307328
                  AND m.msg_type < 20 AND m.msg_id > 0
                  AND m.state = 0
                GROUP BY
                    conversation_id,
                    target_id,
                    conversation_name,
                    conversation_avatar UNION ALL
                SELECT
                    COUNT( m.msg_id ) unread_count,
                    MAX( m.msg_id ) last_msg_id,
                    m.conversation_id conversation_id,
                    c.target_id target_id,
                    c.conversation_name conversation_name,
                    c.conversation_avatar conversation_avatar
                FROM
                    t_user_message m
                        RIGHT JOIN t_user_conversation c ON m.conversation_id = c.conversation_id
                        AND m.user_id = c.user_id
                WHERE
                        m.user_id = 406355803890966528
                  AND m.conversation_id = 406487153854307328
                  AND m.msg_type < 20 AND m.msg_id > 0
                  AND m.state = 1
                GROUP BY
                    conversation_id,
                    target_id,
                    conversation_name,
                    conversation_avatar
            ) t
        LIMIT 1