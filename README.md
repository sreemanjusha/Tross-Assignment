# Tross-Assignment
Tross Assignment submission

# LinkedIn Profile API

## Overview

A Spring Boot API that accepts a LinkedIn profile URL and returnsstructured profile information.

## Tech Stack

- Java 21
- Spring Boot
- Maven
- Jackson
- RestClient

-------------------------------------------------------------------

## API

POST- /api/v1/profile

### Request
*This is an example, I have used my own profile link for this*

{
  "profileUrl": "https://www.linkedin.com/in/veeramalla-sree-manjusha/"
}

### Response
*This is a sample response, which I got for my profile*

{
    "source_url": "https://www.linkedin.com/in/veeramalla-sree-manjusha/",
    "profile": {
        "public_id": "veeramalla-sree-manjusha",
        "profile_urn": "urn:li:fsd_profile:ACoAADAI7_0B-O8MoQQ7j2BfV2iueQxkMfnJw2A",
        "name": "Veeramalla Sree Manjusha",
        "first_name": "Veeramalla",
        "last_name": "Sree Manjusha",
        "headline": "SDE II @Standard Chartered GBS India | Backend Engineering | API Architecture | Quarkus | DBMS| IIT(ISM) Dhanbad | ML Enthusiast",
        "location": "Bengaluru, Karnataka, India",
        "about": "Manjusha is a skilled Senior Analyst Developer with over 2 years of experience at Standard Chartered Bank (SCB), specializing in backend engineering, API design, and database management. She has a strong track record of architecting and implementing scalable, high-performance solutions using technologies such as Quarkus, Microservices, and DBMS. With in-depth knowledge of the Software Development Life Cycle (SDLC), Manjusha plays a key role in the successful delivery of complex projects within agile, cross-functional teams. Focused on system optimization and performance enhancement, Manjusha is committed to developing innovative and efficient solutions that drive both organizational success and long-term value.\n\nOutside of work, Manjusha enjoys exploring her passion for cooking and expressing her creativity through art.",
        "profile_image": null,
        "experience": [
            {
                "title": "Software engineer developer 2",
                "company": "Standard Chartered India",
                "company_url": "https://www.linkedin.com/company/standard-chartered-india/",
                "location": "Bangalore Urban, Karnataka, India",
                "description": "• Building scalable microservices and APIs\n• Driving system design discussions and improving architecture\n• Writing high-quality, maintainable code in Java\n• Collaborating with cross-functional teams to deliver reliable, customer-focused solutions",
                "from": "2025-09",
                "to": "present"
            },
            {
                "title": "Development Engineer",
                "company": "Standard Chartered India",
                "company_url": "https://www.linkedin.com/company/standard-chartered-india/",
                "location": "Bengaluru, Karnataka, India",
                "description": "• Developing RESTful and GraphQL APIs, optimizing database queries with Quarkus and Oracle DB to build scalable backend systems.\n• Delivered 40+ enhancements and resolved 80+ bugs on the PEGA platform, significantly improving user experience and driving a 5% business impact.\n• Developed and presented 3 end-to-end proof-of-concept solutions using internal framework, Lit.js, Java, and Quarkus, demonstrating scalable and efficient technical approaches.\n• Collaborated cross-functionally with teams to ensure seamless integration and timely delivery of backend solutions aligned with business needs.\n• Actively contributed to improving code quality and reducing system downtimes through rigorous testing, code reviews, and performance optimizations.\n• Enhanced business performance by 2% by optimizing user experience (UX) and delivering 10+ PEGA features, reducing defects and improving system stability.\n• Contributed to the development of two critical internal tools: one built with Python Dash for advanced data visualization and another leveraging the SC Toolkit low-code platform for process automation.",
                "from": "2023-07",
                "to": "2025-09"
            },
            {
                "title": "Technology Intern",
                "company": "Standard Chartered Bank",
                "company_url": "https://www.linkedin.com/company/standardchartered/",
                "location": "Chennai, Tamil Nadu, India",
                "description": "• Group ERMF effectiveness Review Automation - built an API connection.\n• Built a web page using SpringBoot framework and React for accessing the data and displaying the outcome in an effective way.\n• Predictive Modelling on the Loss Data to Predict and project future losses based on the information from internal Risk Events.",
                "from": "2022-05",
                "to": "2022-07"
            }
        ],
        "education": [
            {
                "school": "Indian Institute of Technology (Indian School of Mines), Dhanbad",
                "school_url": "https://www.linkedin.com/school/iitism/",
                "degree": "Bachelor of Technology",
                "field_of_study": "Electrical and Electronics Engineering",
                "description": "GPA: 7.51",
                "from": "2019",
                "to": "2023"
            },
            {
                "school": "MP and EV English Medium School",
                "school_url": null,
                "degree": "Class 12",
                "field_of_study": "Science",
                "description": null,
                "from": "2017-08",
                "to": "2019-05"
            },
            {
                "school": "Ravindra Bharathi School",
                "school_url": null,
                "degree": "Class 10",
                "field_of_study": "Science",
                "description": null,
                "from": "2016-08",
                "to": "2017-05"
            }
        ],
        "skills": [
            {
                "name": "JavaScript",
                "endorsements": null
            },
            {
                "name": "REST APIs",
                "endorsements": null
            },
            {
                "name": "Python (Programming Language)",
                "endorsements": null
            },
            {
                "name": "Data Structures",
                "endorsements": null
            },
            {
                "name": "Pegasystems PRPC",
                "endorsements": null
            },
            {
                "name": "PostgreSQL",
                "endorsements": null
            },
            {
                "name": "Kubernetes",
                "endorsements": null
            },
            {
                "name": "Azure DevOps Services",
                "endorsements": null
            },
            {
                "name": "Systems Design",
                "endorsements": null
            },
            {
                "name": "Oracle Database",
                "endorsements": null
            },
            {
                "name": "Node.js",
                "endorsements": null
            },
            {
                "name": "GraphQL",
                "endorsements": null
            },
            {
                "name": "Quarkus",
                "endorsements": null
            },
            {
                "name": "Transact-SQL (T-SQL)",
                "endorsements": null
            },
            {
                "name": "Flask",
                "endorsements": null
            },
            {
                "name": "Java",
                "endorsements": null
            },
            {
                "name": "Springboot",
                "endorsements": null
            },
            {
                "name": "Web Development",
                "endorsements": null
            },
            {
                "name": "Spring Boot",
                "endorsements": null
            },
            {
                "name": "React.js",
                "endorsements": null
            }
        ],
        "certifications": [],
        "languages": [],
        "raw_available_sections": {
            "included_entities": 78,
            "has_position_groups": true,
            "has_educations": true,
            "has_skills": true,
            "has_certifications": true,
            "has_languages": true
        },
        "source": {
            "url": "https://www.linkedin.com/in/veeramalla-sree-manjusha/",
            "endpoint": "/voyager/api/identity/dash/profiles",
            "decoration_id": "FullProfileWithEntities-101"
        }
    }
}

-----------------------------------------------------------

## Setup

### Environment Variables

LINKEDIN_LI_AT
LINKEDIN_JSESSIONID

### Run locally

mvn spring-boot:run

-------------------------------------------------------

## Architecture

Explain:

Controller
   ↓
Service
   ↓
LinkedIn Client
   ↓
LinkedIn endpoint
   ↓
Parser
   ↓
Structured response

--------------------------------------------------------------

## Reverse Engineering Approach

I inspected LinkedIn web application using Chrome DevTools Network tab to identify the underlying HTTP endpoints and request payloads.

The application directly calls those endpoints using HTTP and does not use a browser automation framework.

## Known Limitations

- LinkedIn may change internal endpoints.
- Authentication/session credentials are required.
- Some profile information may not be available depending on the target profile and LinkedIn visibility settings.
- LinkedIn may rate-limit or block requests.
- The implementation depends on LinkedIn's current internal APIs.
- 
------------------------------------------------------------------------------

## Security

Please note that the Credentials are supplied through environment variables and are not committed to source control.
