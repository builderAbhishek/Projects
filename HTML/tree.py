from turtle import Screen, Turtle, Vec2D

THICKNESS = 30

def draw_line(position, angle, length, size, color):
    turtle.penup()
    turtle.goto(position)
    turtle.setheading(angle)
    turtle.color(color)
    turtle.pensize(size)
    turtle.pendown()
    turtle.forward(length)

def draw_tree(position, angle, length, size, color, n):
    if n == 0:
        return

    if n <= 3:
        color = 'blue'

    draw_line(position, angle, length, size, color)

    position = turtle.position()
    length /= 1.4
    size *= 0.7

    draw_tree(position, angle - THICKNESS, length, size, color, n - 1)
    draw_tree(position, angle, length, size, color, n - 1)
    draw_tree(position, angle + THICKNESS, length, size, color, n - 1)

screen = Screen()
screen.tracer(False)

turtle = Turtle(visible=False)

draw_tree(Vec2D(0, -350), 90, 150, 10, 'black', 9)

screen.update()
screen.tracer(True)
screen.exitonclick()