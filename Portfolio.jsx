import { useEffect, useState } from "react";
import "./portfolio.css";

const NAV_LINKS = ["About", "Projects", "Skills", "Education", "Achievements", "Resume", "Contact"];

const PROJECTS = [
  {
    title: "Campus Sync",
    tech: ["Django REST", "React", "TypeScript", "PostgreSQL", "Tailwind CSS", "JWT"],
    desc: "A full-stack campus management platform with role-based access, club and event workflows, live messaging, smart search, and an admin dashboard.",
    live: "https://campus-project-eight.vercel.app/",
    api: "https://campus-sync-rjbz.onrender.com/api/",
    label: "Flagship Build",
    year: "2025",
  },
  {
    title: "Gesture Play: Motion Racing",
    tech: ["Python", "OpenCV", "MediaPipe", "Unity", "C#", "UDP"],
    desc: "A touchless 3D racing game where hand landmarks are detected with MediaPipe and streamed into Unity for responsive steering and acceleration.",
    live: null,
    api: null,
    label: "HCI Experiment",
    year: "2025",
  },
  {
    title: "ATM Simulator System",
    tech: ["Java", "Swing", "AWT", "MySQL"],
    desc: "A desktop banking simulation covering account creation, PIN authentication, deposits, withdrawals, and mini statements with relational data integrity.",
    live: null,
    api: null,
    label: "Desktop App",
    year: "2024",
  },
];

const SKILLS = {
  Languages: ["Python", "Java", "JavaScript", "TypeScript", "SQL", "HTML", "CSS"],
  Frameworks: ["Django", "Django REST", "React", "Tailwind CSS"],
  Tools: ["MySQL", "PostgreSQL", "Git", "Cloudinary", "JWT", "OpenCV", "MediaPipe", "Unity"],
  Certifications: [
    "Oracle Cloud Infrastructure 2025 AI Foundations Associate",
    "ServiceNow Micro-Certification",
  ],
};

const ACHIEVEMENTS = [
  {
    place: "2nd",
    event: "DSA Command - Inter-University DSA Competition",
    year: "2025",
    desc: "Ranked among the best participants across multiple institutions by solving algorithmic problems under intense time pressure.",
  },
  {
    place: "3rd",
    event: "Front-End Fusion - Universum University-Level UI/UX Contest",
    year: "2025",
    desc: "Recognized for rapid prototyping, interface clarity, and strong UI decision making in a competitive design round.",
  },
  {
    place: "1st",
    event: "National Space Day Quiz",
    year: "2024",
    desc: "Secured the top national rank in a knowledge competition focused on space science and satellite technology.",
  },
  {
    place: "2nd",
    event: "National Space Day Quiz",
    year: "2025",
    desc: "Maintained top-tier performance in a national-level STEM competition for the second consecutive year.",
  },
  {
    place: "3rd",
    event: "Republic Day Quiz Championship",
    year: "2026",
    desc: "Finished on the podium in a high-participation university quiz event that rewarded speed, recall, and breadth of knowledge.",
  },
  {
    place: "3rd",
    event: "Inter-University Badminton Tournament",
    year: "2026",
    desc: "Represented MMEC Mullana and earned a podium finish through disciplined teamwork, strategy, and consistency.",
  },
];

const QUICK_FACTS = [
  { label: "Based In", value: "Mullana, Haryana" },
  { label: "Origin", value: "Nepal" },
  { label: "Degree", value: "B.E. Computer Engineering" },
  { label: "CGPA", value: "87.75%" },
];

const HIGHLIGHTS = [
  "Builds full-stack web applications with a strong backend foundation.",
  "Enjoys problem solving, competitive coding, and HCI-driven experiments.",
  "Balances engineering, design sensibility, and communication in projects.",
];

const EDUCATION = [
  {
    period: "2022 - Present",
    title: "B.E. in Computer Engineering",
    institution: "Maharishi Markandeshwar (Deemed to be University), Mullana",
    detail: "Focused on software engineering, databases, object-oriented programming, and practical full-stack product development.",
    meta: "CGPA: 87.75%",
  },
  {
    period: "2020 - 2022",
    title: "Higher Secondary Education",
    institution: "National Infotech Secondary School, Nepal",
    detail: "Built a strong academic base in mathematics, science, and problem solving that shaped my engineering journey.",
    meta: "Science Stream",
  },
  {
    period: "2019 - 2020",
    title: "Secondary Education",
    institution: "Shree Secondary School, Nepal",
    detail: "Developed early interest in technology, competition, and structured analytical learning.",
    meta: "SEE Completed",
  },
];

export default function Portfolio() {
  const [activeSection, setActiveSection] = useState("about");
  const [menuOpen, setMenuOpen] = useState(false);
  const [theme, setTheme] = useState("light");

  useEffect(() => {
    const savedTheme = window.localStorage.getItem("portfolio-theme");
    if (savedTheme === "dark" || savedTheme === "light") {
      setTheme(savedTheme);
    }
  }, []);

  useEffect(() => {
    document.documentElement.setAttribute("data-theme", theme);
    window.localStorage.setItem("portfolio-theme", theme);
  }, [theme]);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            setActiveSection(entry.target.id);
          }
        });
      },
      { threshold: 0.45 }
    );

    const sections = document.querySelectorAll("section[id]");
    sections.forEach((section) => observer.observe(section));

    return () => observer.disconnect();
  }, []);

  const scrollTo = (id) => {
    document.getElementById(id.toLowerCase())?.scrollIntoView({ behavior: "smooth", block: "start" });
    setMenuOpen(false);
  };

  return (
    <div className="pf-root">
      <nav className="pf-nav">
        <button type="button" className="pf-brand" onClick={() => scrollTo("about")}>
          <span className="pf-brand-mark">SK</span>
          <span className="pf-brand-copy">
            <strong>Sanjay Kumar Sah</strong>
            <span>Portfolio</span>
          </span>
        </button>

        <ul className={`pf-nav-links ${menuOpen ? "open" : ""}`}>
          {NAV_LINKS.map((link) => (
            <li key={link}>
              <button
                type="button"
                className={activeSection === link.toLowerCase() ? "active" : ""}
                onClick={() => scrollTo(link)}
              >
                {link}
              </button>
            </li>
          ))}
        </ul>

        <button
          type="button"
          className="pf-theme-toggle"
          onClick={() => setTheme((current) => (current === "light" ? "dark" : "light"))}
          aria-label={`Switch to ${theme === "light" ? "dark" : "light"} mode`}
        >
          <span>{theme === "light" ? "Dark" : "Light"}</span>
        </button>

        <button
          type="button"
          className={`pf-hamburger ${menuOpen ? "open" : ""}`}
          onClick={() => setMenuOpen((prev) => !prev)}
          aria-label="Toggle menu"
        >
          <span />
          <span />
        </button>
      </nav>

      <main className="pf-main">
        <section id="about" className="pf-hero">
          <div className="pf-hero-copy">
            <p className="pf-kicker">Computer engineering student / developer / problem solver</p>
            <h1>
              Building clean developer experiences
            </h1>
            <button type="button" className="pf-scroll-cue" onClick={() => scrollTo("projects")}>
              Scroll Down
            </button>
            <p className="pf-hero-text">
              I am Sanjay Kumar Sah, a Computer Engineering student at MM(DU) Mullana focused on building
              polished web applications, practical systems, and interfaces that feel thoughtful to use.
            </p>
            <p className="pf-hero-text">
              My work lives at the intersection of software engineering, product thinking, and competitive
              problem solving, with roots in Nepal and a mindset aimed at global impact.
            </p>

            <div className="pf-hero-actions">
              <a href="mailto:Sahsanjay2006@gmail.com" className="pf-btn pf-btn-primary">
                Start a conversation
              </a>
              <a href="/Resume.pdf" download className="pf-btn pf-btn-secondary">
                Download resume
              </a>
            </div>
          </div>

          <div className="pf-hero-panel">
            <div className="pf-portrait">
              <div className="pf-portrait-frame">
                <span>SKS</span>
              </div>
            </div>

            <div className="pf-intro-card">
              <p>Focused on full-stack development, UI craftsmanship, and building reliable products from idea to deployment.</p>
              <a href="https://www.linkedin.com/in/sanjay-kumar-sah-52b392294" target="_blank" rel="noreferrer">
                Connect on LinkedIn
              </a>
            </div>
          </div>
        </section>

        <section className="pf-facts-band" aria-label="Quick facts">
          {QUICK_FACTS.map((fact) => (
            <div className="pf-fact" key={fact.label}>
              <span>{fact.label}</span>
              <strong>{fact.value}</strong>
            </div>
          ))}
        </section>

        <section id="projects" className="pf-section pf-section-split">
          <div className="pf-section-intro">
            <p className="pf-kicker">Selected work</p>
            <h2>Projects that show how I think and build.</h2>
            <p>
              These projects reflect my interest in scalable backend systems, interface quality, and solving
              real user problems with practical engineering.
            </p>
          </div>

          <div className="pf-project-list">
            {PROJECTS.map((project) => (
              <article className="pf-project-item" key={project.title}>
                <div className="pf-project-meta">
                  <span>{project.label}</span>
                  <strong>{project.year}</strong>
                </div>
                <div className="pf-project-body">
                  <h3>{project.title}</h3>
                  <p>{project.desc}</p>
                  <div className="pf-chip-row">
                    {project.tech.map((tech) => (
                      <span className="pf-chip" key={tech}>
                        {tech}
                      </span>
                    ))}
                  </div>
                  <div className="pf-project-links">
                    {project.live && (
                      <a href={project.live} target="_blank" rel="noreferrer">
                        View project
                      </a>
                    )}
                    {project.api && (
                      <a href={project.api} target="_blank" rel="noreferrer">
                        API endpoint
                      </a>
                    )}
                  </div>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section id="skills" className="pf-section pf-section-split">
          <div className="pf-section-intro">
            <p className="pf-kicker">Capabilities</p>
            <h2>A mix of engineering depth and design awareness.</h2>
            <p>
              I like tools that help me move from idea to working product quickly, while still paying attention
              to maintainability and user experience.
            </p>
          </div>

          <div className="pf-skills-area">
            {Object.entries(SKILLS).map(([category, items]) => (
              <div className="pf-skill-block" key={category}>
                <h3>{category}</h3>
                <div className="pf-chip-row">
                  {items.map((item) => (
                    <span className="pf-chip pf-chip-soft" key={item}>
                      {item}
                    </span>
                  ))}
                </div>
              </div>
            ))}

            <div className="pf-highlight-block">
              <h3>What I bring</h3>
              <ul>
                {HIGHLIGHTS.map((item) => (
                  <li key={item}>{item}</li>
                ))}
              </ul>
            </div>
          </div>
        </section>

        <section id="education" className="pf-section pf-section-split">
          <div className="pf-section-intro">
            <p className="pf-kicker">Education</p>
            <h2>Academic milestones that shaped my technical foundation.</h2>
            <p>
              My education path reflects steady growth in engineering fundamentals, practical computing, and
              the curiosity that continues to drive my project work.
            </p>
          </div>

          <div className="pf-education-timeline">
            {EDUCATION.map((item) => (
              <article className="pf-education-item" key={`${item.title}-${item.period}`}>
                <div className="pf-education-rail">
                  <span className="pf-education-dot" />
                  <span className="pf-education-line" />
                </div>
                <div className="pf-education-card">
                  <span className="pf-education-period">{item.period}</span>
                  <h3>{item.title}</h3>
                  <strong>{item.institution}</strong>
                  <p>{item.detail}</p>
                  <span className="pf-education-meta">{item.meta}</span>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section id="achievements" className="pf-section pf-section-split">
          <div className="pf-section-intro">
            <p className="pf-kicker">Recognition</p>
            <h2>Competitive results across coding, design, and academics.</h2>
            <p>
              I enjoy environments that reward clarity under pressure, whether that means algorithms, design
              sprints, quiz competitions, or sports.
            </p>
          </div>

          <div className="pf-timeline">
            {ACHIEVEMENTS.map((achievement) => (
              <article className="pf-timeline-item" key={`${achievement.event}-${achievement.year}`}>
                <div className="pf-timeline-year">{achievement.year}</div>
                <div className="pf-timeline-content">
                  <span className="pf-badge">{achievement.place}</span>
                  <h3>{achievement.event}</h3>
                  <p>{achievement.desc}</p>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section id="resume" className="pf-section">
          <div className="pf-resume-card">
            <div>
              <p className="pf-kicker">Resume</p>
              <h2>Need the full profile in one place?</h2>
              <p>
                Download my resume for a concise view of projects, coursework, certifications, and achievements.
              </p>
            </div>
            <div className="pf-resume-actions">
              <a href="/Resume.pdf" download className="pf-btn pf-btn-primary">
                Download PDF
              </a>
              <a href="/Resume.pdf" target="_blank" rel="noreferrer" className="pf-btn pf-btn-secondary">
                Open in browser
              </a>
            </div>
          </div>
        </section>

        <section id="contact" className="pf-section">
          <div className="pf-contact-panel">
            <div className="pf-contact-copy">
              <p className="pf-kicker">Contact</p>
              <h2>Let&apos;s build something useful together.</h2>
              <p>
                I&apos;m open to internships, collaborations, and strong product ideas where engineering and
                thoughtful execution both matter.
              </p>
            </div>

            <div className="pf-contact-grid">
              <a href="mailto:Sahsanjay2006@gmail.com" className="pf-contact-card">
                <span>Email</span>
                <strong>Sahsanjay2006@gmail.com</strong>
              </a>
              <a href="tel:+917783835165" className="pf-contact-card">
                <span>Phone</span>
                <strong>+91 77838 35165</strong>
              </a>
              <a
                href="https://www.linkedin.com/in/sanjay-kumar-sah-52b392294"
                target="_blank"
                rel="noreferrer"
                className="pf-contact-card"
              >
                <span>LinkedIn</span>
                <strong>sanjay-kumar-sah</strong>
              </a>
            </div>
          </div>
        </section>
      </main>

      <footer className="pf-footer">
        <span>Sanjay Kumar Sah</span>
        <span>React Portfolio</span>
      </footer>
    </div>
  );
}
