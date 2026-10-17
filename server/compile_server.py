# 编译后端示例（Python + arduino-cli）
# 依赖：pip install flask; 且已安装 arduino-cli 并配置好板子支持

from flask import Flask, request, jsonify
import subprocess, tempfile, os

app = Flask(__name__)

@app.route('/compile', methods=['POST'])
def compile_sketch():
    data = request.get_json()
    fqbn = data.get('fqbn', 'arduino:avr:uno')
    code = data.get('code', '')

    with tempfile.TemporaryDirectory() as d:
        sketch_dir = os.path.join(d, 'sketch')
        os.makedirs(sketch_dir)
        with open(os.path.join(sketch_dir, 'sketch.ino'), 'w') as f:
            f.write(code)

        r = subprocess.run(
            ['arduino-cli', 'compile', '--fqbn', fqbn, sketch_dir],
            capture_output=True, text=True
        )
        if r.returncode != 0:
            return jsonify({'ok': False, 'log': r.stderr})

        # 找到产物（hex/bin）
        firmware = ''
        ftype = 'hex'
        for root, _, files in os.walk(d):
            for fn in files:
                if fn.endswith('.hex'):
                    firmware = open(os.path.join(root, fn)).read()
                    ftype = 'hex'
                elif fn.endswith('.bin'):
                    firmware = open(os.path.join(root, fn), 'rb').read().hex()
                    ftype = 'bin'

        return jsonify({'ok': True, 'firmware': firmware, 'type': ftype, 'log': r.stdout})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)
