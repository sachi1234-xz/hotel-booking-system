export default function About() {
  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
      <div className="text-center mb-12">
        <h1 className="text-4xl font-bold text-gray-900 mb-4">About StayEase</h1>
        <p className="text-xl text-gray-600">Your trusted partner in finding the perfect stay</p>
      </div>

      <div className="prose prose-lg mx-auto text-gray-600 space-y-6">
        <p>StayEase was founded with a simple mission: make hotel booking effortless. We connect travelers with the best hotels worldwide, offering a seamless booking experience from search to check-out.</p>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 my-12">
          {[
            { icon: '🎯', title: 'Our Mission', desc: 'To provide a seamless, transparent, and enjoyable hotel booking experience for every traveler.' },
            { icon: '🌍', title: 'Global Reach', desc: 'We partner with hotels across the globe to bring you the best options at competitive prices.' },
            { icon: '💡', title: 'Innovation', desc: 'We leverage modern technology to make your booking experience fast, secure, and intuitive.' },
            { icon: '🤝', title: 'Customer First', desc: 'Our dedicated support team is always ready to help you with any questions or concerns.' },
          ].map((item, i) => (
            <div key={i} className="bg-gray-50 rounded-2xl p-6">
              <span className="text-3xl">{item.icon}</span>
              <h3 className="text-lg font-bold text-gray-900 mt-3 mb-2">{item.title}</h3>
              <p className="text-gray-600">{item.desc}</p>
            </div>
          ))}
        </div>

        <div className="bg-indigo-50 rounded-2xl p-8 text-center mt-12">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Built With Modern Tech</h2>
          <p className="text-gray-600 mb-6">Our platform is powered by Spring Boot microservices, PostgreSQL, React, and Docker - ensuring reliability, scalability, and performance.</p>
          <div className="flex flex-wrap justify-center gap-4">
            {['Spring Boot', 'React', 'PostgreSQL', 'Docker', 'Tailwind CSS'].map((tech) => (
              <span key={tech} className="bg-white text-indigo-700 px-4 py-2 rounded-full text-sm font-medium shadow-sm">{tech}</span>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
