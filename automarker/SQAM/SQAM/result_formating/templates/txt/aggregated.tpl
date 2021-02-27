Autotested Results for {{ result.name }}
Generated at {{ result.date }}
--

Summary of Results: {{ result.results | length }} assignments graded

{% for test in result.results %}{% if test.results %}{{ test.name}}: {{ test.results | get_all_counts(0) }}/{{ test.results | get_all_counts(3) }} passed
{% endif %}{% endfor %}
