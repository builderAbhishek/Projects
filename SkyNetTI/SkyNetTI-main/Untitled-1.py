class CareerGuidanceBot:
    def __init__(self):
        self.questions = [
            "Aapke pasandida subjects kaun-kaun se hain? (Science, Arts, Commerce, Others, None) ",
            "Aapko kis type ke tasks karne mein interest hai - creative, analytical ya people-oriented? (Creative, Analytical, People-oriented, All, None) ",
            "Kya aapko technology ke saath kaam karne mein interest hai? (Yes, No, Maybe, Not Sure, Partially) ",
            "Aapki strengths kya hain? (Leadership, Communication, Problem-solving, Creativity, Analytical) ",
            "Aapki weaknesses kya hain? (Time Management, Public Speaking, Attention to Detail, Patience, Teamwork) ",
            "Aapko teamwork pasand hai ya individual work? (Teamwork, Individual Work, Both, None, Depends) ",
            "Aapko leadership roles mein interest hai ya aap chahte hain ki aapka focus apne expertise par ho? (Leadership, Expertise, Both, None, Not Sure) ",
            "Aapko communication skills mein confidence hai? (Yes, No, Somewhat, Working On It, Not Sure) ",
            "Aapko public speaking karna pasand hai ya nahi? (Yes, No, Sometimes, Not Sure, Never Tried) ",
            "Kya aapka focus practical skills par hai ya theoretical knowledge par? (Practical Skills, Theoretical Knowledge, Balanced, Not Sure, Depends) ",
            "Aapko fieldwork karna pasand hai ya aapko office environment pasand hai? (Fieldwork, Office Environment, Both, None, Depends) ",
            "Aapko research karna pasand hai ya nahi? (Yes, No, Maybe, Not Sure, Occasionally) ",
            "Aapko problem-solving mein maza aata hai ya nahi? (Yes, No, Sometimes, Not Sure, Rarely) ",
            "Kya aapko design aur creativity mein interest hai? (Yes, No, Maybe, Not Sure, Partially) ",
            "Aapko numbers aur data analysis samajhne mein maza aata hai ya nahi? (Yes, No, Sometimes, Not Sure, Rarely) ",
            "Kya aapka interest business aur entrepreneurship mein hai? (Yes, No, Maybe, Not Sure, Partially) ",
            "Aapko stress handling aur time management mein expertise hai? (Yes, No, Working On It, Not Sure, Partially) ",
            "Aapko long-term career goals kya hain? (Entrepreneurship, Leadership, Specialization, Research, Not Decided) ",
            "Aapne koi internships ya work experiences ki hain? (Yes, No, Maybe, Not Yet, Planning) ",
            "Kya aapko higher studies karne ka interest hai ya nahi? (Yes, No, Maybe, Not Sure, Already Decided) "
        ]
        self.answers = {}

    def start(self):
        print("Welcome to the Career Guidance Bot!")
        print("Please select the most appropriate options for the following questions:")

        for question in self.questions:
            print(question)
            answer = input("Your response: ")
            self.answers[question] = answer

        self.suggest_career()

    def suggest_career(self):
        print("\nBased on your responses, we suggest considering the following career options:")
        # Add decision-making logic based on the answers to suggest suitable career options

if __name__ == '__main__':
    bot = CareerGuidanceBot()
    bot.start()
