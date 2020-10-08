import os
from flask import Flask, render_template, request


app = Flask(__name__)

@app.route("/helloWorld")
def hello():
  return 'Hello World!'

# Serve the HTMl file
@app.route('/')
def upload_form():
    return render_template('upload.html')


# Main route to get confirmaton for file upload
@app.route('/', methods=['POST'])
def upload_file():
    if request.method == 'POST':
        print("Got here")

if __name__ == '__main__':
  app.run(host='127.0.0.1',port=5050,debug=False,threaded=True)