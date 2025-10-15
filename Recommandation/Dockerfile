FROM python:3.11-slim

WORKDIR /app

# Installer dépendances système nécessaires
RUN apt-get update && apt-get install -y \
    build-essential \
    python3-dev \
    gcc \
    g++ \
    libopenblas-dev \
    liblapack-dev \
    curl \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt /app/

# Upgrade pip
RUN python -m pip install --upgrade pip setuptools wheel

# Installer UNIQUEMENT depuis requirements.txt (supprimez la ligne torch séparée)
RUN pip install -r requirements.txt --no-cache-dir

COPY . /app

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]