package service;

import io.grpc.stub.StreamObserver;
import proto.UserGrpc;
import proto.UserOuterClass;

import java.time.LocalDate;
import java.time.Period;

public class UserImpl extends UserGrpc.UserImplBase {
    /*
     * We observe here that some words have an "@", this are Annotations. Annotations are used to provide supplement
     * information about a program. We can autogenerate this functions, in Intellij we can use the shortcut ctrl + O to
     * do this.
     * */

    public String getGender(String tin) {
        String gender = "";

        switch (tin.charAt(0)) {
            case '1':
            case '3':
            case '5':
            case '7': {
                gender = "male";
                break;
            }

            case '2':
            case '4':
            case '6':
            case '8': {
                gender = "female";
                break;
            }

        }
        return gender;

    }

    public boolean validateTin(String tin) {
        if (tin.length() != 13) {
            return false;
        }

        if (getGender(tin) == "") {
            return false;
        }

        return true;

    }

    public String getYear(String tin) {
        String year = tin.substring(1, 3);

        switch (tin.charAt(0)) {
            case '1':
            case '2':
            case '7':
            case '8': {
                year = "19" + year;
                break;
            }

            case '3':
            case '4': {
                year = "18" + year;
                break;
            }

            case '5':
            case '6': {
                year = "20" + year;
                break;
            }

        }

       return year;
    }

    public Period getAge(String tin) {
        String monthString = tin.substring(3, 5);
        String dayString = tin.substring(5, 7);
        String yearString= getYear(tin);

        int month= Integer.parseInt(monthString);
        int day=Integer.parseInt(dayString);
        int year=Integer.parseInt(yearString);

       LocalDate birthdate= LocalDate.of(year, month, day);

       LocalDate today = LocalDate.now();

       Period age = Period.between(birthdate, today);

       return age;

    }


    @Override
    public void getInfo(UserOuterClass.InfoRequest request, StreamObserver<UserOuterClass.InfoReply> responseObserver) {

        String tin = request.getTin();

        if (!validateTin(tin)) {
            UserOuterClass.InfoReply reply = UserOuterClass.InfoReply.newBuilder().setMessage(request.getName() +
                    " your TIN is invalid ").build();
            /* We can call multiple times onNext function if we have multiple replies, ex. in next commits */
            responseObserver.onNext(reply);

        } else {
            String gender = getGender(tin);
            Period period = getAge(tin);

            String age = period.getYears()+" year/s " + period.getMonths() +  " month/s " + period.getDays() + " day/s";

            UserOuterClass.InfoReply reply = UserOuterClass.InfoReply.newBuilder().setMessage("Hello " + request.getName()
                    + " you have " + age + " and your gender is " + gender + '\n').build();
            /* We can call multiple times onNext function if we have multiple replies, ex. in next commits */
            responseObserver.onNext(reply);

        }
        /* We use the response observer's onCompleted method to specify that we've finished dealing with the RPC */

        System.out.println("Information has been delivered");
        responseObserver.onCompleted();
    }

}
